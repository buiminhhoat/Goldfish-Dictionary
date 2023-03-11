package com.goldfish_dictionary;

public class Vocabulary {
    String id;
    String word;
    String ipa;
    String meaning;

    public Vocabulary() {

    }

    public Vocabulary(String word, String ipa, String meaning) {
        this.word = word;
        this.ipa = ipa;
        this.meaning = meaning;
    }

    public Vocabulary(String id, String word, String ipa, String meaning) {
        this.id = id;
        this.word = word;
        this.ipa = ipa;
        this.meaning = meaning;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

}
