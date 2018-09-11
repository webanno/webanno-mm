
/* First created by JCasGen Wed Jun 21 10:58:39 CEST 2017 */
package de.uhh.lt.webanno.exmaralda.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Thu Jan 25 17:37:17 CET 2018
 * @generated */
public class Incident_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Incident.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.uhh.lt.webanno.exmaralda.type.Incident");
 
  /** @generated */
  final Feature casFeat_Desc;
  /** @generated */
  final int     casFeatCode_Desc;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDesc(int addr) {
        if (featOkTst && casFeat_Desc == null)
      jcas.throwFeatMissing("Desc", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Desc);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDesc(int addr, String v) {
        if (featOkTst && casFeat_Desc == null)
      jcas.throwFeatMissing("Desc", "de.uhh.lt.webanno.exmaralda.type.Incident");
    ll_cas.ll_setStringValue(addr, casFeatCode_Desc, v);}
    
  
 
  /** @generated */
  final Feature casFeat_StartID;
  /** @generated */
  final int     casFeatCode_StartID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getStartID(int addr) {
        if (featOkTst && casFeat_StartID == null)
      jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return ll_cas.ll_getStringValue(addr, casFeatCode_StartID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStartID(int addr, String v) {
        if (featOkTst && casFeat_StartID == null)
      jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    ll_cas.ll_setStringValue(addr, casFeatCode_StartID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_EndID;
  /** @generated */
  final int     casFeatCode_EndID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getEndID(int addr) {
        if (featOkTst && casFeat_EndID == null)
      jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return ll_cas.ll_getStringValue(addr, casFeatCode_EndID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEndID(int addr, String v) {
        if (featOkTst && casFeat_EndID == null)
      jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    ll_cas.ll_setStringValue(addr, casFeatCode_EndID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SpeakerID;
  /** @generated */
  final int     casFeatCode_SpeakerID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSpeakerID(int addr) {
        if (featOkTst && casFeat_SpeakerID == null)
      jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SpeakerID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpeakerID(int addr, String v) {
        if (featOkTst && casFeat_SpeakerID == null)
      jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.Incident");
    ll_cas.ll_setStringValue(addr, casFeatCode_SpeakerID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_IncidentType;
  /** @generated */
  final int     casFeatCode_IncidentType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getIncidentType(int addr) {
        if (featOkTst && casFeat_IncidentType == null)
      jcas.throwFeatMissing("IncidentType", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return ll_cas.ll_getStringValue(addr, casFeatCode_IncidentType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIncidentType(int addr, String v) {
        if (featOkTst && casFeat_IncidentType == null)
      jcas.throwFeatMissing("IncidentType", "de.uhh.lt.webanno.exmaralda.type.Incident");
    ll_cas.ll_setStringValue(addr, casFeatCode_IncidentType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_isTextual;
  /** @generated */
  final int     casFeatCode_isTextual;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsTextual(int addr) {
        if (featOkTst && casFeat_isTextual == null)
      jcas.throwFeatMissing("isTextual", "de.uhh.lt.webanno.exmaralda.type.Incident");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_isTextual);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsTextual(int addr, boolean v) {
        if (featOkTst && casFeat_isTextual == null)
      jcas.throwFeatMissing("isTextual", "de.uhh.lt.webanno.exmaralda.type.Incident");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_isTextual, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Incident_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Desc = jcas.getRequiredFeatureDE(casType, "Desc", "uima.cas.String", featOkTst);
    casFeatCode_Desc  = (null == casFeat_Desc) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Desc).getCode();

 
    casFeat_StartID = jcas.getRequiredFeatureDE(casType, "StartID", "uima.cas.String", featOkTst);
    casFeatCode_StartID  = (null == casFeat_StartID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_StartID).getCode();

 
    casFeat_EndID = jcas.getRequiredFeatureDE(casType, "EndID", "uima.cas.String", featOkTst);
    casFeatCode_EndID  = (null == casFeat_EndID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_EndID).getCode();

 
    casFeat_SpeakerID = jcas.getRequiredFeatureDE(casType, "SpeakerID", "uima.cas.String", featOkTst);
    casFeatCode_SpeakerID  = (null == casFeat_SpeakerID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SpeakerID).getCode();

 
    casFeat_IncidentType = jcas.getRequiredFeatureDE(casType, "IncidentType", "uima.cas.String", featOkTst);
    casFeatCode_IncidentType  = (null == casFeat_IncidentType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_IncidentType).getCode();

 
    casFeat_isTextual = jcas.getRequiredFeatureDE(casType, "isTextual", "uima.cas.Boolean", featOkTst);
    casFeatCode_isTextual  = (null == casFeat_isTextual) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isTextual).getCode();

  }
}



    