package de.uhh.lt.webanno.exmaralda.type;

public interface TEIspan {
 
	
  //*--------------*
  //* Feature: SpanType

  /** getter for SpanType - gets 
   * @return value of the feature 
   */
  public String getSpanType(); 
    
  /** setter for SpanType - sets  
   * @param v value to set into the feature 
   */
  public void setSpanType(String v);    
   
    
  //*--------------*
  //* Feature: StartID

  /** getter for StartID - gets 
   * @return value of the feature 
   */
  public String getStartID();
    
  /** setter for StartID - sets  
   * @param v value to set into the feature 
   */
  public void setStartID(String v);
   
    
  //*--------------*
  //* Feature: EndID

  /** getter for EndID - gets 
   * @return value of the feature 
   */
  public String getEndID();
    
  /** setter for EndID - sets  
   * @param v value to set into the feature 
   */
  public void setEndID(String v); 
   
    
  //*--------------*
  //* Feature: Content

  /** getter for Content - gets 
   * @return value of the feature 
   */
  public String getContent(); 
    
  /** setter for Content - sets  
   * @param v value to set into the feature 
   */
  public void setContent(String v); 
  
    
  //*--------------*
  //* Feature: SpeakerID

  /** getter for SpeakerID - gets 
   * @return value of the feature 
   */
  public String getSpeakerID();
  
  
  /** setter for SpeakerID - sets  
   * @param v value to set into the feature 
   */
  public void setSpeakerID(String v);  

}