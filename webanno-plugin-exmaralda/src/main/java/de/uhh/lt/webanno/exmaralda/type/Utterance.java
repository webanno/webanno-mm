

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
public class Utterance extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Utterance.class);
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
  protected Utterance() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Utterance(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Utterance(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Utterance(JCas jcas, int begin, int end) {
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
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Utterance_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setID(String v) {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    jcasType.ll_cas.ll_setStringValue(addr, ((Utterance_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: SpeakerID

  /** getter for SpeakerID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSpeakerID() {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Utterance_Type)jcasType).casFeatCode_SpeakerID);}
    
  /** setter for SpeakerID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpeakerID(String v) {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    jcasType.ll_cas.ll_setStringValue(addr, ((Utterance_Type)jcasType).casFeatCode_SpeakerID, v);}    
   
    
  //*--------------*
  //* Feature: StartID

  /** getter for StartID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getStartID() {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_StartID == null)
      jcasType.jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Utterance_Type)jcasType).casFeatCode_StartID);}
    
  /** setter for StartID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStartID(String v) {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_StartID == null)
      jcasType.jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    jcasType.ll_cas.ll_setStringValue(addr, ((Utterance_Type)jcasType).casFeatCode_StartID, v);}    
   
    
  //*--------------*
  //* Feature: EndID

  /** getter for EndID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getEndID() {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_EndID == null)
      jcasType.jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Utterance_Type)jcasType).casFeatCode_EndID);}
    
  /** setter for EndID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEndID(String v) {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_EndID == null)
      jcasType.jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    jcasType.ll_cas.ll_setStringValue(addr, ((Utterance_Type)jcasType).casFeatCode_EndID, v);}    
   
    
  //*--------------*
  //* Feature: SpeakerABBR

  /** getter for SpeakerABBR - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSpeakerABBR() {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_SpeakerABBR == null)
      jcasType.jcas.throwFeatMissing("SpeakerABBR", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Utterance_Type)jcasType).casFeatCode_SpeakerABBR);}
    
  /** setter for SpeakerABBR - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpeakerABBR(String v) {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_SpeakerABBR == null)
      jcasType.jcas.throwFeatMissing("SpeakerABBR", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    jcasType.ll_cas.ll_setStringValue(addr, ((Utterance_Type)jcasType).casFeatCode_SpeakerABBR, v);}    
  }

    