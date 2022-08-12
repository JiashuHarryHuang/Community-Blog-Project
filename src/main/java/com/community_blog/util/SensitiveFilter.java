package com.community_blog.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SensitiveFilter {
    /**
     * 敏感词打码
     */
    private static final String REPLACEMENT = "***";

    /**
     * 根节点：空字符
     */
    private TrieNode root = new TrieNode();

    /**
     * 初始化方法：加载敏感词文件
     */
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到字典树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            log.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    /**
     * 添加一个字符串到字典树
     * @param keyword 要添加的字符串
     */
    private void addKeyword(String keyword) {
        //用一个指针遍历字典树
        TrieNode cur = root;
        for (int i = 0; i < keyword.length(); ++i) {
            char c = keyword.charAt(i);

            //根据遍历到的字符获取子节点
            TrieNode subNode = root.getSubNode(c);
            if (subNode == null) { //没有子节点，则新建一个节点并添加到字典树
                subNode = new TrieNode();
                cur.addSubNode(c, subNode);
            }

            //更新指针
            cur = subNode;

            //如果是最后一个字符，则标记为末尾字符
            if (i == keyword.length() - 1) {
                cur.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 需要检查的字符串
     * @return 过滤掉敏感词的字符串
     */
    public String filter(String text) {
        StringBuilder resultStr = new StringBuilder();

        //双指针遍历字符串
        int fast = 0;
        int slow = 0;

        //一个指针遍历字典树
        TrieNode cur = root;

        while (fast < text.length()) {
            char c = text.charAt(fast);

            // 跳过敏感词中间的符号：赌*博
            if (isSymbol(c)) {
                // 如果符号不在敏感词之间，则加入resultStr
                if (cur == root) {
                    resultStr.append(c);
                    ++slow;
                }
                ++fast;
                continue;
            }

            cur = cur.getSubNode(c);

            if (cur == null) { //当前子串(fast到slow之间的子串)不是敏感词
                //把slow指向的字符加入resultStr并让fast去找slow然后一起更新
                resultStr.append(text.charAt(slow));
                fast = slow;
                cur = root;
                ++slow;
            } else {
                if (cur.isLastChar) { //当前子串是敏感词
                    //敏感词打码并让slow去找fast然后一起更新
                    resultStr.append(REPLACEMENT);
                    slow = fast;
                    ++slow;
                    cur = root;
                }
            }

            ++fast;
        }

        resultStr.append(text.substring(slow));

        return resultStr.toString();
    }

    /**
     * 判断是否是符号
     * @param c 当前字符
     * @return 判断是否是符号
     */
    private boolean isSymbol(char c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 字典树
     */
    private class TrieNode {
        /**
         * 当前节点是否是最后一个字母
         */
        private boolean isLastChar = false;

        /**
         * 下一个字母的节点
         */
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        /**
         * 判断该字符是否是最后一个
         * @return 判断该字符是否是最后一个
         */
        public boolean isKeywordEnd() {
            return isLastChar;
        }

        /**
         * setter
         * @param keywordEnd 判断该字符是否是最后一个
         */
        public void setKeywordEnd(boolean keywordEnd) {
            isLastChar = keywordEnd;
        }

        /**
         * 添加字符/节点
         * @param c 字符
         * @param node 新节点
         */
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        /**
         * 根据字符获取子节点
         * @param c 字符
         * @return 字符对应的子节点
         */
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
