

/* First created by JCasGen Tue Jun 06 14:54:10 CEST 2017 */
package de.uhh.lt.webanno.mm.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Sep 28 09:28:20 CEST 2018
 * XML source: /Users/rem/git/webanno-mm/webanno-mm-plugin/src/main/resources/desc/type/TeiTranscript.xml
 * @generated */
public class Segment extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Segment.class);
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
  protected Segment() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Segment(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Segment(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Segment(JCas jcas, int begin, int end) {
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
  //* Feature: ID

  /** getter for ID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getID() {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.mm.type.Segment");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Segment_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setID(String v) {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.mm.type.Segment");
    jcasType.ll_cas.ll_setStringValue(addr, ((Segment_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: SegmentType

  /** getter for SegmentType - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSegmentType() {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_SegmentType == null)
      jcasType.jcas.throwFeatMissing("SegmentType", "de.uhh.lt.webanno.mm.type.Segment");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Segment_Type)jcasType).casFeatCode_SegmentType);}
    
  /** setter for SegmentType - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSegmentType(String v) {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_SegmentType == null)
      jcasType.jcas.throwFeatMissing("SegmentType", "de.uhh.lt.webanno.mm.type.Segment");
    jcasType.ll_cas.ll_setStringValue(addr, ((Segment_Type)jcasType).casFeatCode_SegmentType, v);}    
   
    
  //*--------------*
  //* Feature: SegmentSubtype

  /** getter for SegmentSubtype - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSegmentSubtype() {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_SegmentSubtype == null)
      jcasType.jcas.throwFeatMissing("SegmentSubtype", "de.uhh.lt.webanno.mm.type.Segment");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Segment_Type)jcasType).casFeatCode_SegmentSubtype);}
    
  /** setter for SegmentSubtype - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSegmentSubtype(String v) {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_SegmentSubtype == null)
      jcasType.jcas.throwFeatMissing("SegmentSubtype", "de.uhh.lt.webanno.mm.type.Segment");
    jcasType.ll_cas.ll_setStringValue(addr, ((Segment_Type)jcasType).casFeatCode_SegmentSubtype, v);}    
   
    
  //*--------------*
  //* Feature: UtteranceID

  /** getter for UtteranceID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getUtteranceID() {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_UtteranceID == null)
      jcasType.jcas.throwFeatMissing("UtteranceID", "de.uhh.lt.webanno.mm.type.Segment");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Segment_Type)jcasType).casFeatCode_UtteranceID);}
    
  /** setter for UtteranceID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUtteranceID(String v) {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_UtteranceID == null)
      jcasType.jcas.throwFeatMissing("UtteranceID", "de.uhh.lt.webanno.mm.type.Segment");
    jcasType.ll_cas.ll_setStringValue(addr, ((Segment_Type)jcasType).casFeatCode_UtteranceID, v);}    
   
    
  //*--------------*
  //* Feature: SpeakerID

  /** getter for SpeakerID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSpeakerID() {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.mm.type.Segment");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Segment_Type)jcasType).casFeatCode_SpeakerID);}
    
  /** setter for SpeakerID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpeakerID(String v) {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.mm.type.Segment");
    jcasType.ll_cas.ll_setStringValue(addr, ((Segment_Type)jcasType).casFeatCode_SpeakerID, v);}    
   
    
  //*--------------*
  //* Feature: SentenceNumber

  /** getter for SentenceNumber - gets 
   * @generated
   * @return value of the feature 
   */
  public int getSentenceNumber() {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_SentenceNumber == null)
      jcasType.jcas.throwFeatMissing("SentenceNumber", "de.uhh.lt.webanno.mm.type.Segment");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Segment_Type)jcasType).casFeatCode_SentenceNumber);}
    
  /** setter for SentenceNumber - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentenceNumber(int v) {
    if (Segment_Type.featOkTst && ((Segment_Type)jcasType).casFeat_SentenceNumber == null)
      jcasType.jcas.throwFeatMissing("SentenceNumber", "de.uhh.lt.webanno.mm.type.Segment");
    jcasType.ll_cas.ll_setIntValue(addr, ((Segment_Type)jcasType).casFeatCode_SentenceNumber, v);}    
  }

    