import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] filenames = {"testcase1.json", "testcase2.json"};
        for (String filename : filenames) {
            Map<String, Object> json = simpleJSONParse(readFile(filename));
            Map<String, Object> keys = (Map<String, Object>)json.get("keys");
            int n = ((Number)keys.get("n")).intValue();
            int k = ((Number)keys.get("k")).intValue();

            List<Integer> xs = new ArrayList<>();
            List<BigInteger> ys = new ArrayList<>();

            for (int i = 1; i <= n; ++i) {
                String iStr = Integer.toString(i);
                if (!json.containsKey(iStr)) continue;
                Map<String, Object> pt = (Map<String, Object>)json.get(iStr);
                int base = Integer.parseInt((String)pt.get("base"));
                String value = (String)pt.get("value");
                int x = i;
                BigInteger y = new BigInteger(value, base);
                xs.add(x);
                ys.add(y);
            }

            BigInteger secret = lagrangeAtZero(xs.subList(0, k), ys.subList(0, k));
            System.out.println(secret.toString());
        }
    }

    static BigInteger lagrangeAtZero(List<Integer> xs, List<BigInteger> ys) {
        int k = xs.size();
        BigInteger res = BigInteger.ZERO;
        for (int j = 0; j < k; ++j) {
            BigInteger numer = BigInteger.ONE;
            BigInteger denom = BigInteger.ONE;
            for (int m = 0; m < k; ++m) {
                if (m == j) continue;
                numer = numer.multiply(BigInteger.valueOf(-xs.get(m)));
                denom = denom.multiply(BigInteger.valueOf(xs.get(j) - xs.get(m)));
            }
            BigInteger frac = ys.get(j).multiply(numer).divide(denom);
            res = res.add(frac);
        }
        return res;
    }

    static String readFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String ln;
        while ((ln = br.readLine()) != null) sb.append(ln);
        br.close();
        return sb.toString();
    }


    static Map<String, Object> simpleJSONParse(String s) {
        int[] pos = {0};
        skipWhitespace(s, pos);
        return parseObject(s, pos);
    }
    static Map<String, Object> parseObject(String s, int[] pos) {
        Map<String, Object> m = new HashMap<>();
        expectChar(s, pos, '{');
        skipWhitespace(s, pos);
        while (s.charAt(pos[0]) != '}') {
            String key = parseString(s, pos);
            skipWhitespace(s, pos);
            expectChar(s, pos, ':');
            skipWhitespace(s, pos);
            Object val;
            char c = s.charAt(pos[0]);
            if (c == '{') {
                val = parseObject(s, pos);
            } else if (c == '"') {
                val = parseString(s, pos);
            } else if (Character.isDigit(c) || c == '-') {
                val = parseNumber(s, pos);
            } else {
                throw new RuntimeException("Unexpected char: " + c);
            }
            m.put(key, val);
            skipWhitespace(s, pos);
            if (s.charAt(pos[0]) == ',') {
                pos[0]++;
                skipWhitespace(s, pos);
            } else {
                break;
            }
        }
        expectChar(s, pos, '}');
        return m;
    }
    static String parseString(String s, int[] pos) {
        expectChar(s, pos, '"');
        StringBuilder sb = new StringBuilder();
        while (s.charAt(pos[0]) != '"') {
            sb.append(s.charAt(pos[0]));
            pos[0]++;
        }
        pos[0]++;
        return sb.toString();
    }
    static Number parseNumber(String s, int[] pos) {
        int start = pos[0];
        if (s.charAt(pos[0]) == '-') pos[0]++;
        while (pos[0] < s.length() && (Character.isDigit(s.charAt(pos[0])))) pos[0]++;
        return Integer.parseInt(s.substring(start, pos[0]));
    }
    static void skipWhitespace(String s, int[] pos) {
        while (pos[0] < s.length() && Character.isWhitespace(s.charAt(pos[0]))) pos[0]++;
    }
    static void expectChar(String s, int[] pos, char c) {
        skipWhitespace(s, pos);
        if (s.charAt(pos[0]) != c)
            throw new RuntimeException("Expected '" + c + "' at pos " + pos[0]);
        pos[0]++;
    }
}