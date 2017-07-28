
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
 * Updated by JCasGen Thu Jul 27 16:36:53 CEST 2017
 * @generated */
public class TEIspan_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TEIspan.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.uhh.lt.webanno.exmaralda.type.TEIspan");
 
  /** @generated */
  final Feature casFeat_SpanType;
  /** @generated */
  final int     casFeatCode_SpanType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSpanType(int addr) {
        if (featOkTst && casFeat_SpanType == null)
      jcas.throwFeatMissing("SpanType", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SpanType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpanType(int addr, String v) {
        if (featOkTst && casFeat_SpanType == null)
      jcas.throwFeatMissing("SpanType", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    ll_cas.ll_setStringValue(addr, casFeatCode_SpanType, v);}
    
  
 
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
      jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    return ll_cas.ll_getStringValue(addr, casFeatCode_StartID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStartID(int addr, String v) {
        if (featOkTst && casFeat_StartID == null)
      jcas.throwFeatMissing("StartID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
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
      jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    return ll_cas.ll_getStringValue(addr, casFeatCode_EndID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEndID(int addr, String v) {
        if (featOkTst && casFeat_EndID == null)
      jcas.throwFeatMissing("EndID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    ll_cas.ll_setStringValue(addr, casFeatCode_EndID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Content;
  /** @generated */
  final int     casFeatCode_Content;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getContent(int addr) {
        if (featOkTst && casFeat_Content == null)
      jcas.throwFeatMissing("Content", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Content);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setContent(int addr, String v) {
        if (featOkTst && casFeat_Content == null)
      jcas.throwFeatMissing("Content", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    ll_cas.ll_setStringValue(addr, casFeatCode_Content, v);}
    
  
 
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
      jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SpeakerID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpeakerID(int addr, String v) {
        if (featOkTst && casFeat_SpeakerID == null)
      jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.TEIspan");
    ll_cas.ll_setStringValue(addr, casFeatCode_SpeakerID, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TEIspan_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_SpanType = jcas.getRequiredFeatureDE(casType, "SpanType", "uima.cas.String", featOkTst);
    casFeatCode_SpanType  = (null == casFeat_SpanType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SpanType).getCode();

 
    casFeat_StartID = jcas.getRequiredFeatureDE(casType, "StartID", "uima.cas.String", featOkTst);
    casFeatCode_StartID  = (null == casFeat_StartID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_StartID).getCode();

 
    casFeat_EndID = jcas.getRequiredFeatureDE(casType, "EndID", "uima.cas.String", featOkTst);
    casFeatCode_EndID  = (null == casFeat_EndID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_EndID).getCode();

 
    casFeat_Content = jcas.getRequiredFeatureDE(casType, "Content", "uima.cas.String", featOkTst);
    casFeatCode_Content  = (null == casFeat_Content) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Content).getCode();

 
    casFeat_SpeakerID = jcas.getRequiredFeatureDE(casType, "SpeakerID", "uima.cas.String", featOkTst);
    casFeatCode_SpeakerID  = (null == casFeat_SpeakerID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SpeakerID).getCode();

  }
}



    