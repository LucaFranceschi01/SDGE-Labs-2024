package edu.upf.filter;

public interface LanguageFilter {

    /**
     * Process
     * @param language
     * @return
     */
    Long filterLanguage(String language) throws Exception;
}