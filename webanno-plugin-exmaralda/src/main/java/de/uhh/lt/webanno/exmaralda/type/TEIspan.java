

/* First created by JCasGen Tue Jun 06 14:54:10 CEST 2017 */
package de.uhh.lt.webanno.exmaralda.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Jun 07 14:49:15 CEST 2017
 * XML source: /Users/rem/git/webanno-exmaralda/webanno-plugin-exmaralda/src/main/resources/desc/type/TeiTranscript.xml
 * @generated */
public class TEIspan extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TEIspan.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected TEIspan() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TEIspan(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TEIspan(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TEIspan(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: SpanType

  /** getter for SpanType - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSpanType() {
    if (TEIspan_Type.featOkTst && ((TEIspan_Type)jcasType).casFeat_SpanType == null)
      jcasType.jcas.throwFeatMissing("SpanType", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TEIspan_Type)jcasType).casFeatCode_SpanType);}
    
  /** setter for SpanType - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpanType(String v) {
    if (TEIspan_Type.featOkTst && ((TEIspan_Type)jcasType).casFeat_SpanType == null)
      jcasType.jcas.throwFeatMissing("SpanType", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    jcasType.ll_cas.ll_setStringValue(addr, ((TEIspan_Type)jcasType).casFeatCode_SpanType, v);}    
   
    
  //*--------------*
  //* Feature: StartID

  /** getter for StartID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getStartID() {
    if (TEIspan_Type.featOkTst && ((TEIspan_Type)jcasType).casFeat_StartID == null)
      jcasType.jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TEIspan_Type)jcasType).casFeatCode_StartID);}
    
  /** setter for StartID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStartID(String v) {
    if (TEIspan_Type.featOkTst && ((TEIspan_Type)jcasType).casFeat_StartID == null)
      jcasType.jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    jcasType.ll_cas.ll_setStringValue(addr, ((TEIspan_Type)jcasType).casFeatCode_StartID, v);}    
   
    
  //*--------------*
  //* Feature: EndID

  /** getter for EndID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getEndID() {
    if (TEIspan_Type.featOkTst && ((TEIspan_Type)jcasType).casFeat_EndID == null)
      jcasType.jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TEIspan_Type)jcasType).casFeatCode_EndID);}
    
  /** setter for EndID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEndID(String v) {
    if (TEIspan_Type.featOkTst && ((TEIspan_Type)jcasType).casFeat_EndID == null)
      jcasType.jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    jcasType.ll_cas.ll_setStringValue(addr, ((TEIspan_Type)jcasType).casFeatCode_EndID, v);}    
   
    
  //*--------------*
  //* Feature: Content

  /** getter for Content - gets 
   * @generated
   * @return value of the feature 
   */
  public String getContent() {
    if (TEIspan_Type.featOkTst && ((TEIspan_Type)jcasType).casFeat_Content == null)
      jcasType.jcas.throwFeatMissing("Content", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TEIspan_Type)jcasType).casFeatCode_Content);}
    
  /** setter for Content - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setContent(String v) {
    if (TEIspan_Type.featOkTst && ((TEIspan_Type)jcasType).casFeat_Content == null)
      jcasType.jcas.throwFeatMissing("Content", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    jcasType.ll_cas.ll_setStringValue(addr, ((TEIspan_Type)jcasType).casFeatCode_Content, v);}    
   
    
  //*--------------*
  //* Feature: SpeakerID

  /** getter for SpeakerID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSpeakerID() {
    if (TEIspan_Type.featOkTst && ((TEIspan_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TEIspan_Type)jcasType).casFeatCode_SpeakerID);}
    
  /** setter for SpeakerID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpeakerID(String v) {
    if (TEIspan_Type.featOkTst && ((TEIspan_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    jcasType.ll_cas.ll_setStringValue(addr, ((TEIspan_Type)jcasType).casFeatCode_SpeakerID, v);}    
  }

    