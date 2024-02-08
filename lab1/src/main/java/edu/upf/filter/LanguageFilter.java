package edu.upf.filter;

public interface LanguageFilter {

  /**
   * Process
   * @param language
   * @return
   */
  Integer filterLanguage(String language) throws Exception;
}
