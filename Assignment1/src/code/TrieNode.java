package code;

import java.util.*;

public class TrieNode {
    private boolean isWord = false;
    private Map<String, TrieNode> children;

    public TrieNode(){
        this.children = new HashMap<String, TrieNode>();
    }

    /**
     * add all the stop name to Trie
     * @param stopName
     */
    public void add(String stopName){
        TrieNode root = this;
        for(String letter: stopName.split("")){
            if(root.children.get(letter) == null){
                root.children.put(letter, new TrieNode());
            }
            root = root.children.get(letter);
        }
        root.isWord = true;
    }

    /**
     * read the prefix, set the root node to the last letter of the prefix
     * @param prefix
     * @return
     */
    public List<String> getAll(String prefix){
        List<String> result = new ArrayList<String>();
        TrieNode root = this;
        for(String letter: prefix.split("")){
            if(root.children.get(letter) == null){
                return null;
            }
            root = root.children.get(letter);
        }
        getAllFrom(root, prefix, result);
        return result;
    }


    public void getAllFrom(TrieNode root, String prefix, List<String> result){
        for(String letter: root.children.keySet()){
            StringBuilder word = new StringBuilder(prefix.toString());
            word.append(letter);
            //if(root.children.get(letter).isWord)
            result.add(word.toString());
            getAllFrom(root.children.get(letter), word.toString(), result);
        }

    }
}
