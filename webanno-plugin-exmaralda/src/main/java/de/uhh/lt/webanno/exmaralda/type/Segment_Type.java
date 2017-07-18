
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
public class Segment_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Segment.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.uhh.lt.webanno.exmaralda.type.Segment");
 
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
      jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.exmaralda.type.Segment");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setID(int addr, String v) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.exmaralda.type.Segment");
    ll_cas.ll_setStringValue(addr, casFeatCode_ID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SegmentType;
  /** @generated */
  final int     casFeatCode_SegmentType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSegmentType(int addr) {
        if (featOkTst && casFeat_SegmentType == null)
      jcas.throwFeatMissing("SegmentType", "de.uhh.lt.webanno.exmaralda.type.Segment");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SegmentType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSegmentType(int addr, String v) {
        if (featOkTst && casFeat_SegmentType == null)
      jcas.throwFeatMissing("SegmentType", "de.uhh.lt.webanno.exmaralda.type.Segment");
    ll_cas.ll_setStringValue(addr, casFeatCode_SegmentType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SegmentSubtype;
  /** @generated */
  final int     casFeatCode_SegmentSubtype;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSegmentSubtype(int addr) {
        if (featOkTst && casFeat_SegmentSubtype == null)
      jcas.throwFeatMissing("SegmentSubtype", "de.uhh.lt.webanno.exmaralda.type.Segment");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SegmentSubtype);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSegmentSubtype(int addr, String v) {
        if (featOkTst && casFeat_SegmentSubtype == null)
      jcas.throwFeatMissing("SegmentSubtype", "de.uhh.lt.webanno.exmaralda.type.Segment");
    ll_cas.ll_setStringValue(addr, casFeatCode_SegmentSubtype, v);}
    
  
 
  /** @generated */
  final Feature casFeat_UtteranceID;
  /** @generated */
  final int     casFeatCode_UtteranceID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getUtteranceID(int addr) {
        if (featOkTst && casFeat_UtteranceID == null)
      jcas.throwFeatMissing("UtteranceID", "de.uhh.lt.webanno.exmaralda.type.Segment");
    return ll_cas.ll_getStringValue(addr, casFeatCode_UtteranceID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUtteranceID(int addr, String v) {
        if (featOkTst && casFeat_UtteranceID == null)
      jcas.throwFeatMissing("UtteranceID", "de.uhh.lt.webanno.exmaralda.type.Segment");
    ll_cas.ll_setStringValue(addr, casFeatCode_UtteranceID, v);}
    
  
 
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
      jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.Segment");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SpeakerID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpeakerID(int addr, String v) {
        if (featOkTst && casFeat_SpeakerID == null)
      jcas.throwFeatMissing("SpeakerID", "de.uhh.lt.webanno.exmaralda.type.Segment");
    ll_cas.ll_setStringValue(addr, casFeatCode_SpeakerID, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Segment_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ID = jcas.getRequiredFeatureDE(casType, "ID", "uima.cas.String", featOkTst);
    casFeatCode_ID  = (null == casFeat_ID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ID).getCode();

 
    casFeat_SegmentType = jcas.getRequiredFeatureDE(casType, "SegmentType", "uima.cas.String", featOkTst);
    casFeatCode_SegmentType  = (null == casFeat_SegmentType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SegmentType).getCode();

 
    casFeat_SegmentSubtype = jcas.getRequiredFeatureDE(casType, "SegmentSubtype", "uima.cas.String", featOkTst);
    casFeatCode_SegmentSubtype  = (null == casFeat_SegmentSubtype) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SegmentSubtype).getCode();

 
    casFeat_UtteranceID = jcas.getRequiredFeatureDE(casType, "UtteranceID", "uima.cas.String", featOkTst);
    casFeatCode_UtteranceID  = (null == casFeat_UtteranceID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_UtteranceID).getCode();

 
    casFeat_SpeakerID = jcas.getRequiredFeatureDE(casType, "SpeakerID", "uima.cas.String", featOkTst);
    casFeatCode_SpeakerID  = (null == casFeat_SpeakerID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SpeakerID).getCode();

  }
}



    