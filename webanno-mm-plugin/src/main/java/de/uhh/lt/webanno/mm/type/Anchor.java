

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
public class Anchor extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Anchor.class);
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
  protected Anchor() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Anchor(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Anchor(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Anchor(JCas jcas, int begin, int end) {
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
    if (Anchor_Type.featOkTst && ((Anchor_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.mm.type.Anchor");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Anchor_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setID(String v) {
    if (Anchor_Type.featOkTst && ((Anchor_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.mm.type.Anchor");
    jcasType.ll_cas.ll_setStringValue(addr, ((Anchor_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: UtteranceID

  /** getter for UtteranceID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getUtteranceID() {
    if (Anchor_Type.featOkTst && ((Anchor_Type)jcasType).casFeat_UtteranceID == null)
      jcasType.jcas.throwFeatMissing("UtteranceID", "de.uhh.lt.webanno.mm.type.Anchor");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Anchor_Type)jcasType).casFeatCode_UtteranceID);}
    
  /** setter for UtteranceID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUtteranceID(String v) {
    if (Anchor_Type.featOkTst && ((Anchor_Type)jcasType).casFeat_UtteranceID == null)
      jcasType.jcas.throwFeatMissing("UtteranceID", "de.uhh.lt.webanno.mm.type.Anchor");
    jcasType.ll_cas.ll_setStringValue(addr, ((Anchor_Type)jcasType).casFeatCode_UtteranceID, v);}    
   
    
  //*--------------*
  //* Feature: SpeakerID

  /** getter for SpeakerID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSpeakerID() {
    if (Anchor_Type.featOkTst && ((Anchor_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.mm.type.Anchor");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Anchor_Type)jcasType).casFeatCode_SpeakerID);}
    
  /** setter for SpeakerID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpeakerID(String v) {
    if (Anchor_Type.featOkTst && ((Anchor_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.mm.type.Anchor");
    jcasType.ll_cas.ll_setStringValue(addr, ((Anchor_Type)jcasType).casFeatCode_SpeakerID, v);}    
   
    
  //*--------------*
  //* Feature: SentenceNumber

  /** getter for SentenceNumber - gets 
   * @generated
   * @return value of the feature 
   */
  public int getSentenceNumber() {
    if (Anchor_Type.featOkTst && ((Anchor_Type)jcasType).casFeat_SentenceNumber == null)
      jcasType.jcas.throwFeatMissing("SentenceNumber", "de.uhh.lt.webanno.mm.type.Anchor");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Anchor_Type)jcasType).casFeatCode_SentenceNumber);}
    
  /** setter for SentenceNumber - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentenceNumber(int v) {
    if (Anchor_Type.featOkTst && ((Anchor_Type)jcasType).casFeat_SentenceNumber == null)
      jcasType.jcas.throwFeatMissing("SentenceNumber", "de.uhh.lt.webanno.mm.type.Anchor");
    jcasType.ll_cas.ll_setIntValue(addr, ((Anchor_Type)jcasType).casFeatCode_SentenceNumber, v);}    
  }

    