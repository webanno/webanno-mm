
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
public class TranscriptionDocumentMetadata_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TranscriptionDocumentMetadata.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.uhh.lt.webanno.exmaralda.type.TranscriptionDocumentMetadata");
 
  /** @generated */
  final Feature casFeat_TeiMetadataByteArray;
  /** @generated */
  final int     casFeatCode_TeiMetadataByteArray;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getTeiMetadataByteArray(int addr) {
        if (featOkTst && casFeat_TeiMetadataByteArray == null)
      jcas.throwFeatMissing("TeiMetadataByteArray", "de.uhh.lt.webanno.exmaralda.type.TranscriptionDocumentMetadata");
    return ll_cas.ll_getRefValue(addr, casFeatCode_TeiMetadataByteArray);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTeiMetadataByteArray(int addr, int v) {
        if (featOkTst && casFeat_TeiMetadataByteArray == null)
      jcas.throwFeatMissing("TeiMetadataByteArray", "de.uhh.lt.webanno.exmaralda.type.TranscriptionDocumentMetadata");
    ll_cas.ll_setRefValue(addr, casFeatCode_TeiMetadataByteArray, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public byte getTeiMetadataByteArray(int addr, int i) {
        if (featOkTst && casFeat_TeiMetadataByteArray == null)
      jcas.throwFeatMissing("TeiMetadataByteArray", "de.uhh.lt.webanno.exmaralda.type.TranscriptionDocumentMetadata");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getByteArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_TeiMetadataByteArray), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_TeiMetadataByteArray), i);
  return ll_cas.ll_getByteArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_TeiMetadataByteArray), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setTeiMetadataByteArray(int addr, int i, byte v) {
        if (featOkTst && casFeat_TeiMetadataByteArray == null)
      jcas.throwFeatMissing("TeiMetadataByteArray", "de.uhh.lt.webanno.exmaralda.type.TranscriptionDocumentMetadata");
    if (lowLevelTypeChecks)
      ll_cas.ll_setByteArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_TeiMetadataByteArray), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_TeiMetadataByteArray), i);
    ll_cas.ll_setByteArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_TeiMetadataByteArray), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TranscriptionDocumentMetadata_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TeiMetadataByteArray = jcas.getRequiredFeatureDE(casType, "TeiMetadataByteArray", "uima.cas.ByteArray", featOkTst);
    casFeatCode_TeiMetadataByteArray  = (null == casFeat_TeiMetadataByteArray) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TeiMetadataByteArray).getCode();

  }
}



    