

/* First created by JCasGen Thu Jan 25 17:26:59 CET 2018 */
package de.uhh.lt.webanno.mm.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Sep 28 09:28:20 CEST 2018
 * XML source: /Users/rem/git/webanno-mm/webanno-mm-plugin/src/main/resources/desc/type/TeiTranscript.xml
 * @generated */
public class TEIspanAkz extends Annotation implements TEIspan {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TEIspanAkz.class);
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
  protected TEIspanAkz() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TEIspanAkz(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TEIspanAkz(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TEIspanAkz(JCas jcas, int begin, int end) {
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
    if (TEIspanAkz_Type.featOkTst && ((TEIspanAkz_Type)jcasType).casFeat_SpanType == null)
      jcasType.jcas.throwFeatMissing("SpanType", "de.uhh.lt.webanno.mm.type.TEIspanAkz");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TEIspanAkz_Type)jcasType).casFeatCode_SpanType);}
    
  /** setter for SpanType - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpanType(String v) {
    if (TEIspanAkz_Type.featOkTst && ((TEIspanAkz_Type)jcasType).casFeat_SpanType == null)
      jcasType.jcas.throwFeatMissing("SpanType", "de.uhh.lt.webanno.mm.type.TEIspanAkz");
    jcasType.ll_cas.ll_setStringValue(addr, ((TEIspanAkz_Type)jcasType).casFeatCode_SpanType, v);}    
   
    
  //*--------------*
  //* Feature: StartID

  /** getter for StartID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getStartID() {
    if (TEIspanAkz_Type.featOkTst && ((TEIspanAkz_Type)jcasType).casFeat_StartID == null)
      jcasType.jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.mm.type.TEIspanAkz");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TEIspanAkz_Type)jcasType).casFeatCode_StartID);}
    
  /** setter for StartID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStartID(String v) {
    if (TEIspanAkz_Type.featOkTst && ((TEIspanAkz_Type)jcasType).casFeat_StartID == null)
      jcasType.jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.mm.type.TEIspanAkz");
    jcasType.ll_cas.ll_setStringValue(addr, ((TEIspanAkz_Type)jcasType).casFeatCode_StartID, v);}    
   
    
  //*--------------*
  //* Feature: EndID

  /** getter for EndID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getEndID() {
    if (TEIspanAkz_Type.featOkTst && ((TEIspanAkz_Type)jcasType).casFeat_EndID == null)
      jcasType.jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.mm.type.TEIspanAkz");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TEIspanAkz_Type)jcasType).casFeatCode_EndID);}
    
  /** setter for EndID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEndID(String v) {
    if (TEIspanAkz_Type.featOkTst && ((TEIspanAkz_Type)jcasType).casFeat_EndID == null)
      jcasType.jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.mm.type.TEIspanAkz");
    jcasType.ll_cas.ll_setStringValue(addr, ((TEIspanAkz_Type)jcasType).casFeatCode_EndID, v);}    
   
    
  //*--------------*
  //* Feature: Content

  /** getter for Content - gets 
   * @generated
   * @return value of the feature 
   */
  public String getContent() {
    if (TEIspanAkz_Type.featOkTst && ((TEIspanAkz_Type)jcasType).casFeat_Content == null)
      jcasType.jcas.throwFeatMissing("Content", "de.uhh.lt.webanno.mm.type.TEIspanAkz");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TEIspanAkz_Type)jcasType).casFeatCode_Content);}
    
  /** setter for Content - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setContent(String v) {
    if (TEIspanAkz_Type.featOkTst && ((TEIspanAkz_Type)jcasType).casFeat_Content == null)
      jcasType.jcas.throwFeatMissing("Content", "de.uhh.lt.webanno.mm.type.TEIspanAkz");
    jcasType.ll_cas.ll_setStringValue(addr, ((TEIspanAkz_Type)jcasType).casFeatCode_Content, v);}    
   
    
  //*--------------*
  //* Feature: SpeakerID

  /** getter for SpeakerID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSpeakerID() {
    if (TEIspanAkz_Type.featOkTst && ((TEIspanAkz_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.mm.type.TEIspanAkz");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TEIspanAkz_Type)jcasType).casFeatCode_SpeakerID);}
    
  /** setter for SpeakerID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpeakerID(String v) {
    if (TEIspanAkz_Type.featOkTst && ((TEIspanAkz_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.mm.type.TEIspanAkz");
    jcasType.ll_cas.ll_setStringValue(addr, ((TEIspanAkz_Type)jcasType).casFeatCode_SpeakerID, v);}    
  }

    