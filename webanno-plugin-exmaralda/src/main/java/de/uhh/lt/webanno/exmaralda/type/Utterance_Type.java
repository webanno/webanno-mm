
/* First created by JCasGen Tue Jun 06 14:54:10 CEST 2017 */
package de.uhh.lt.webanno.exmaralda.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Jul 18 21:29:17 CEST 2017
 * @generated */
public class Utterance_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Utterance.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.uhh.lt.webanno.exmaralda.type.Utterance");
 
  /** @generated */
  final Feature casFeat_ID;
  /** @generated */
  final int     casFeatCode_ID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getID(int addr) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setID(int addr, String v) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    ll_cas.ll_setStringValue(addr, casFeatCode_ID, v);}
    
  
 
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
      jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SpeakerID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpeakerID(int addr, String v) {
        if (featOkTst && casFeat_SpeakerID == null)
      jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    ll_cas.ll_setStringValue(addr, casFeatCode_SpeakerID, v);}
    
  
 
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
      jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    return ll_cas.ll_getStringValue(addr, casFeatCode_StartID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStartID(int addr, String v) {
        if (featOkTst && casFeat_StartID == null)
      jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
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
      jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    return ll_cas.ll_getStringValue(addr, casFeatCode_EndID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEndID(int addr, String v) {
        if (featOkTst && casFeat_EndID == null)
      jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    ll_cas.ll_setStringValue(addr, casFeatCode_EndID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SpeakerABBR;
  /** @generated */
  final int     casFeatCode_SpeakerABBR;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSpeakerABBR(int addr) {
        if (featOkTst && casFeat_SpeakerABBR == null)
      jcas.throwFeatMissing("SpeakerABBR", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SpeakerABBR);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpeakerABBR(int addr, String v) {
        if (featOkTst && casFeat_SpeakerABBR == null)
      jcas.throwFeatMissing("SpeakerABBR", "de.uhh.lt.webanno.exmaralda.type.Utterance");
    ll_cas.ll_setStringValue(addr, casFeatCode_SpeakerABBR, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Utterance_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ID = jcas.getRequiredFeatureDE(casType, "ID", "uima.cas.String", featOkTst);
    casFeatCode_ID  = (null == casFeat_ID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ID).getCode();

 
    casFeat_SpeakerID = jcas.getRequiredFeatureDE(casType, "SpeakerID", "uima.cas.String", featOkTst);
    casFeatCode_SpeakerID  = (null == casFeat_SpeakerID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SpeakerID).getCode();

 
    casFeat_StartID = jcas.getRequiredFeatureDE(casType, "StartID", "uima.cas.String", featOkTst);
    casFeatCode_StartID  = (null == casFeat_StartID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_StartID).getCode();

 
    casFeat_EndID = jcas.getRequiredFeatureDE(casType, "EndID", "uima.cas.String", featOkTst);
    casFeatCode_EndID  = (null == casFeat_EndID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_EndID).getCode();

 
    casFeat_SpeakerABBR = jcas.getRequiredFeatureDE(casType, "SpeakerABBR", "uima.cas.String", featOkTst);
    casFeatCode_SpeakerABBR  = (null == casFeat_SpeakerABBR) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SpeakerABBR).getCode();

  }
}



    