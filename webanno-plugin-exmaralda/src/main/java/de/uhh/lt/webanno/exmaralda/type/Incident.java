

/* First created by JCasGen Wed Jun 21 10:58:39 CEST 2017 */
package de.uhh.lt.webanno.exmaralda.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Aug 04 15:44:24 CEST 2017
 * XML source: /Users/rem/git/webanno-exmaralda/webanno-plugin-exmaralda/src/main/resources/desc/type/TeiTranscript.xml
 * @generated */
public class Incident extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Incident.class);
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
  protected Incident() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Incident(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Incident(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Incident(JCas jcas, int begin, int end) {
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
  //* Feature: Desc

  /** getter for Desc - gets 
   * @generated
   * @return value of the feature 
   */
  public String getDesc() {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_Desc == null)
      jcasType.jcas.throwFeatMissing("Desc", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Incident_Type)jcasType).casFeatCode_Desc);}
    
  /** setter for Desc - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDesc(String v) {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_Desc == null)
      jcasType.jcas.throwFeatMissing("Desc", "de.uhh.lt.webanno.exmaralda.type.Incident");
    jcasType.ll_cas.ll_setStringValue(addr, ((Incident_Type)jcasType).casFeatCode_Desc, v);}    
   
    
  //*--------------*
  //* Feature: StartID

  /** getter for StartID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getStartID() {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_StartID == null)
      jcasType.jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Incident_Type)jcasType).casFeatCode_StartID);}
    
  /** setter for StartID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStartID(String v) {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_StartID == null)
      jcasType.jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    jcasType.ll_cas.ll_setStringValue(addr, ((Incident_Type)jcasType).casFeatCode_StartID, v);}    
   
    
  //*--------------*
  //* Feature: EndID

  /** getter for EndID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getEndID() {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_EndID == null)
      jcasType.jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Incident_Type)jcasType).casFeatCode_EndID);}
    
  /** setter for EndID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEndID(String v) {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_EndID == null)
      jcasType.jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    jcasType.ll_cas.ll_setStringValue(addr, ((Incident_Type)jcasType).casFeatCode_EndID, v);}    
   
    
  //*--------------*
  //* Feature: SpeakerID

  /** getter for SpeakerID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSpeakerID() {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Incident_Type)jcasType).casFeatCode_SpeakerID);}
    
  /** setter for SpeakerID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpeakerID(String v) {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_SpeakerID == null)
      jcasType.jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    jcasType.ll_cas.ll_setStringValue(addr, ((Incident_Type)jcasType).casFeatCode_SpeakerID, v);}    
   
    
  //*--------------*
  //* Feature: IncidentType

  /** getter for IncidentType - gets 
   * @generated
   * @return value of the feature 
   */
  public String getIncidentType() {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_IncidentType == null)
      jcasType.jcas.throwFeatMissing("IncidentType", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Incident_Type)jcasType).casFeatCode_IncidentType);}
    
  /** setter for IncidentType - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIncidentType(String v) {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_IncidentType == null)
      jcasType.jcas.throwFeatMissing("IncidentType", "de.uhh.lt.webanno.exmaralda.type.Incident");
    jcasType.ll_cas.ll_setStringValue(addr, ((Incident_Type)jcasType).casFeatCode_IncidentType, v);}    
   
    
  //*--------------*
  //* Feature: isTextual

  /** getter for isTextual - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIsTextual() {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_isTextual == null)
      jcasType.jcas.throwFeatMissing("isTextual", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((Incident_Type)jcasType).casFeatCode_isTextual);}
    
  /** setter for isTextual - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsTextual(boolean v) {
    if (Incident_Type.featOkTst && ((Incident_Type)jcasType).casFeat_isTextual == null)
      jcasType.jcas.throwFeatMissing("isTextual", "de.uhh.lt.webanno.exmaralda.type.Incident");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((Incident_Type)jcasType).casFeatCode_isTextual, v);}    
  }

    