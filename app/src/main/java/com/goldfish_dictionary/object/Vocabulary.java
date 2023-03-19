package com.goldfish_dictionary;

public class Vocabulary {
    String word_id;
    String word;
    String ipa;
    String meaning;

    String name_database;

    public Vocabulary() {

    }

    public Vocabulary(String word, String ipa, String meaning) {
        this.word = word;
        this.ipa = ipa;
        this.meaning = meaning;
    }

    public Vocabulary(String id, String word, String ipa, String meaning) {
        this.word_id = id;
        this.word = word;
        this.ipa = ipa;
        this.meaning = meaning;
    }

    public String getWord_id() {
        return word_id;
    }

    public void setWord_id(String word_id) {
        this.word_id = word_id;
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

    public String getName_database() {
        return name_database;
    }

    public void setName_database(String name_database) {
        this.name_database = name_database;
    }
}
