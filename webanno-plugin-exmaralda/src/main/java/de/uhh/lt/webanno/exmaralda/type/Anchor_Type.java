
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
public class Anchor_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Anchor.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.uhh.lt.webanno.exmaralda.type.Anchor");
 
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
      jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.exmaralda.type.Anchor");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setID(int addr, String v) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "de.uhh.lt.webanno.exmaralda.type.Anchor");
    ll_cas.ll_setStringValue(addr, casFeatCode_ID, v);}
    
  
 
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
      jcas.throwFeatMissing("UtteranceID", "de.uhh.lt.webanno.exmaralda.type.Anchor");
    return ll_cas.ll_getStringValue(addr, casFeatCode_UtteranceID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUtteranceID(int addr, String v) {
        if (featOkTst && casFeat_UtteranceID == null)
      jcas.throwFeatMissing("UtteranceID", "de.uhh.lt.webanno.exmaralda.type.Anchor");
    ll_cas.ll_setStringValue(addr, casFeatCode_UtteranceID, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Anchor_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ID = jcas.getRequiredFeatureDE(casType, "ID", "uima.cas.String", featOkTst);
    casFeatCode_ID  = (null == casFeat_ID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ID).getCode();

 
    casFeat_UtteranceID = jcas.getRequiredFeatureDE(casType, "UtteranceID", "uima.cas.String", featOkTst);
    casFeatCode_UtteranceID  = (null == casFeat_UtteranceID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_UtteranceID).getCode();

  }
}



    