package parsesrcdata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

class Parser {

    public final String workspace = "./";
    public String srcJsonPath;

    private static final String drivername = "com.mysql.cj.jdbc.Driver";
    private static String host = "192.168.83.100"; // ipv6 address not supported
    private static final String databaseName = "MiniDouban";
    private static final String url = "jdbc:mysql://" + host + ":3306/" + databaseName;
    private static final String username = "dba";
    private static final String passwd = "94434275";

    private static final String specificPublishersPattern = "(.*(BasicBooks|BBCDigitalAudio|大塊文化|書林|人民邮电|龍馬文化|二見書房|時報文化|读客文化|人民文学|当代世界|长江文艺|华文|译林|陕西师大|上海译文|角川|电子工业).*)|";
    private static final String publisherPattern = specificPublishersPattern
            + "(.*(工作室|集团|书局|书店|書店|書局|出版|公司|社|馆|Publish|Press|Ltd|Classics|Company).*)";
    private static final String pubDatePattern = "[0-9]{4}(\\.|-).*";

    public int parse() {
        int totalRowsAffected = 0;
        StringBuilder srcText = new StringBuilder();
        try {
            try (Scanner inStream = new Scanner(Paths.get(srcJsonPath), StandardCharsets.UTF_8)) {
                while (inStream.hasNext()) {
                    srcText.append(inStream.nextLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Class.forName(drivername);
            try (Connection connection = DriverManager.getConnection(url, username, passwd)) {
                connection.setAutoCommit(false);
                JSONArray entries = JSON.parseArray(srcText.toString());
                String insertBook = "INSERT ignore INTO Book VALUES(?,?,?,?,?,?,?,?,?,?,?);";
                PreparedStatement insertBookStatement = connection.prepareStatement(insertBook);
                for (int i = 0; i < entries.size(); i++) {
                    try {
                        JSONObject entry = entries.getJSONObject(i);
                        String author_pub = entry.getString("author_pub");
                        String[] info = author_pub.split("/"); // authors/translator, publisher, publish date, price
                        StringBuilder authors = new StringBuilder();
                        int publisherPos;
                        int infoLen = info.length;
                        for (publisherPos = 0; publisherPos < infoLen; publisherPos++) {
                            if (Pattern.matches(publisherPattern, info[publisherPos])) {
                                break;
                            }
                            authors.append(info[publisherPos]);
                        }

                        String[] tmp = entry.getString("信息_链接").split("/");
                        long bookid = Long.parseLong(tmp[tmp.length - 1]);
                        String commentNumStr = entry.getString("评价数");
                        int commentNums = 0;
                        if (!commentNumStr.equals("(目前无人评价)")) {
                            commentNums = Integer.parseInt(commentNumStr.split("人评价")[0].split("\\(少于|\\(")[1]);
                        }
                        insertBookStatement.setString(1, entry.getString("标题"));
                        insertBookStatement.setLong(2, bookid);
                        insertBookStatement.setString(3, entry.getString("图片"));
                        insertBookStatement.setString(4, authors.toString());
                        insertBookStatement.setInt(5, commentNums);
                        insertBookStatement.setString(6, entry.getString("简介"));
                        insertBookStatement.setString(7, entry.getString("信息_链接"));
                        insertBookStatement.setNull(8, Types.FLOAT);
                        // System.out.println(entry.getString("评分"));
                        if (!entry.getString("评分").equals("")) {
                            // System.out.println(entry.getFloatValue("评分"));
                            insertBookStatement.setFloat(8, entry.getFloatValue("评分"));
                        }
                        try {
                            insertBookStatement.setString(9, info[publisherPos]);
                        } catch (Exception e) {
                            System.out.println(Arrays.toString(info));
                            continue;
                        }
                        // insertBookStatement.setString(9, info[publisherPos]);
                        insertBookStatement.setNull(10, Types.CHAR);
                        insertBookStatement.setNull(11, Types.FLOAT);
                        if (infoLen - publisherPos == 1) {
                            continue;
                        }
                        if (infoLen - publisherPos == 2) {
                            if (!Pattern.matches(pubDatePattern, info[publisherPos + 1])) {
                                String price = info[publisherPos + 1];
                                int[] bound = locatePrice(price);
                                insertBookStatement.setFloat(11,
                                        Float.parseFloat(price.substring(bound[0], bound[1] + 1)));
                            } else {
                                try {
                                    insertBookStatement.setString(10, findPubYear(info[publisherPos + 1]));
                                } catch (Exception e) {
                                    System.err.println(Arrays.toString(info));
                                    e.printStackTrace();
                                    continue;
                                }
                            }
                        } else {
                            String price = info[publisherPos + 2];
                            int[] bound = locatePrice(price);
                            try {
                                insertBookStatement.setFloat(11,
                                        Float.parseFloat(price.substring(bound[0], bound[1] + 1)));
                                insertBookStatement.setString(10, findPubYear(info[publisherPos + 1]));
                            } catch (Exception e) {
                                System.err.println(Arrays.toString(info));
                                e.printStackTrace();
                                continue;
                            }
                        }

                        // Savepoint savepoint = connection.setSavepoint();
                        int rowsAffected = 0;
                        try {
                            rowsAffected = insertBookStatement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            connection.rollback(savepoint);
                            continue;
                        }
                        totalRowsAffected += rowsAffected;
                        if (rowsAffected != 0) {
                            connection.commit();
                        }
                        // connection.releaseSavepoint(savepoint); //not supported by jdbc for sql
                        // server
                    } catch (JSONException | ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return totalRowsAffected;
    }

    /**
     * @param priceStr the string contain a book price
     * @return an array contain left bound(inclusive) and right bound(inclusive) of
     *         the price in $priceStr
     */
    public int[] locatePrice(String priceStr) {
        int[] location = new int[2];
        int len = priceStr.length();
        char[] price = priceStr.toCharArray();
        for (location[0] = 0; location[0] < len; location[0]++) {
            if (price[location[0]] >= '0' && price[location[0]] <= '9') {
                break;
            }
        }
        for (location[1] = len - 1; location[1] >= 0; location[1]--) {
            if (price[location[1]] >= '0' && price[location[1]] <= '9') {
                break;
            }
        }
        return location;
    }

    /**
     * @param dateStr A string represent a date, 2000年5月, 2000-5-2号 2000.5.2 eg.
     * @return result of *dateStr* formatted, year omitted will be set 0000, month
     *         omitted will be set to 01, day omitted will be set to 01.
     */
    public String findPubYear(String dateStr) {
        char[] formattedYearStr = new char[4];
        Arrays.fill(formattedYearStr, '0');
        int i;
        for (i = 0; i < dateStr.length(); i++) {
            String subString = dateStr.substring(i, i + 1);
            if (subString.equals("年") || subString.equals("-") || subString.equals(".")) {
                break;
            }
        }
        for (int k = i - 1, j = 3; k >= 0; k--, j--) {
            formattedYearStr[j] = dateStr.substring(k, k + 1).toCharArray()[0];
        }
        return String.valueOf(formattedYearStr);
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Parser parser = new Parser();
        int sum = 0;
        for (int i = 1; i < 7; i++) {
            parser.srcJsonPath = parser.workspace + "src" + i + ".json";
            sum += parser.parse();
        }
        System.out.println(sum);
    }
}
