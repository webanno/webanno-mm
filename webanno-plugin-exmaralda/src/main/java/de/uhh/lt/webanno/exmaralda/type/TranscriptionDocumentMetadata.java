

/* First created by JCasGen Tue Jun 06 14:54:10 CEST 2017 */
package de.uhh.lt.webanno.exmaralda.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.ByteArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Aug 04 15:44:24 CEST 2017
 * XML source: /Users/rem/git/webanno-exmaralda/webanno-plugin-exmaralda/src/main/resources/desc/type/TeiTranscript.xml
 * @generated */
public class TranscriptionDocumentMetadata extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TranscriptionDocumentMetadata.class);
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
  protected TranscriptionDocumentMetadata() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TranscriptionDocumentMetadata(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TranscriptionDocumentMetadata(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TranscriptionDocumentMetadata(JCas jcas, int begin, int end) {
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
  //* Feature: TeiMetadataByteArray

  /** getter for TeiMetadataByteArray - gets 
   * @generated
   * @return value of the feature 
   */
  public ByteArray getTeiMetadataByteArray() {
    if (TranscriptionDocumentMetadata_Type.featOkTst && ((TranscriptionDocumentMetadata_Type)jcasType).casFeat_TeiMetadataByteArray == null)
      jcasType.jcas.throwFeatMissing("TeiMetadataByteArray", "de.uhh.lt.webanno.exmaralda.type.TranscriptionDocumentMetadata");
    return (ByteArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((TranscriptionDocumentMetadata_Type)jcasType).casFeatCode_TeiMetadataByteArray)));}
    
  /** setter for TeiMetadataByteArray - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTeiMetadataByteArray(ByteArray v) {
    if (TranscriptionDocumentMetadata_Type.featOkTst && ((TranscriptionDocumentMetadata_Type)jcasType).casFeat_TeiMetadataByteArray == null)
      jcasType.jcas.throwFeatMissing("TeiMetadataByteArray", "de.uhh.lt.webanno.exmaralda.type.TranscriptionDocumentMetadata");
    jcasType.ll_cas.ll_setRefValue(addr, ((TranscriptionDocumentMetadata_Type)jcasType).casFeatCode_TeiMetadataByteArray, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for TeiMetadataByteArray - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public byte getTeiMetadataByteArray(int i) {
    if (TranscriptionDocumentMetadata_Type.featOkTst && ((TranscriptionDocumentMetadata_Type)jcasType).casFeat_TeiMetadataByteArray == null)
      jcasType.jcas.throwFeatMissing("TeiMetadataByteArray", "de.uhh.lt.webanno.exmaralda.type.TranscriptionDocumentMetadata");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((TranscriptionDocumentMetadata_Type)jcasType).casFeatCode_TeiMetadataByteArray), i);
    return jcasType.ll_cas.ll_getByteArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((TranscriptionDocumentMetadata_Type)jcasType).casFeatCode_TeiMetadataByteArray), i);}

  /** indexed setter for TeiMetadataByteArray - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setTeiMetadataByteArray(int i, byte v) { 
    if (TranscriptionDocumentMetadata_Type.featOkTst && ((TranscriptionDocumentMetadata_Type)jcasType).casFeat_TeiMetadataByteArray == null)
      jcasType.jcas.throwFeatMissing("TeiMetadataByteArray", "de.uhh.lt.webanno.exmaralda.type.TranscriptionDocumentMetadata");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((TranscriptionDocumentMetadata_Type)jcasType).casFeatCode_TeiMetadataByteArray), i);
    jcasType.ll_cas.ll_setByteArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((TranscriptionDocumentMetadata_Type)jcasType).casFeatCode_TeiMetadataByteArray), i, v);}
  }

    