
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
 * Updated by JCasGen Thu Jan 25 17:37:17 CET 2018
 * @generated */
public class PlayableSegmentAnchor_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = PlayableSegmentAnchor.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.uhh.lt.webanno.exmaralda.type.PlayableSegmentAnchor");



  /** @generated */
  final Feature casFeat_Info;
  /** @generated */
  final int     casFeatCode_Info;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getInfo(int addr) {
        if (featOkTst && casFeat_Info == null)
      jcas.throwFeatMissing("Info", "de.uhh.lt.webanno.exmaralda.type.PlayableSegmentAnchor");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Info);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setInfo(int addr, String v) {
        if (featOkTst && casFeat_Info == null)
      jcas.throwFeatMissing("Info", "de.uhh.lt.webanno.exmaralda.type.PlayableSegmentAnchor");
    ll_cas.ll_setStringValue(addr, casFeatCode_Info, v);}
    
  
 
  /** @generated */
  final Feature casFeat_AnchorID;
  /** @generated */
  final int     casFeatCode_AnchorID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAnchorID(int addr) {
        if (featOkTst && casFeat_AnchorID == null)
      jcas.throwFeatMissing("AnchorID", "de.uhh.lt.webanno.exmaralda.type.PlayableSegmentAnchor");
    return ll_cas.ll_getStringValue(addr, casFeatCode_AnchorID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnchorID(int addr, String v) {
        if (featOkTst && casFeat_AnchorID == null)
      jcas.throwFeatMissing("AnchorID", "de.uhh.lt.webanno.exmaralda.type.PlayableSegmentAnchor");
    ll_cas.ll_setStringValue(addr, casFeatCode_AnchorID, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public PlayableSegmentAnchor_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Info = jcas.getRequiredFeatureDE(casType, "Info", "uima.cas.String", featOkTst);
    casFeatCode_Info  = (null == casFeat_Info) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Info).getCode();

 
    casFeat_AnchorID = jcas.getRequiredFeatureDE(casType, "AnchorID", "uima.cas.String", featOkTst);
    casFeatCode_AnchorID  = (null == casFeat_AnchorID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_AnchorID).getCode();

  }
}



    