

/* First created by JCasGen Tue Jun 06 14:54:10 CEST 2017 */
package de.uhh.lt.webanno.exmaralda.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Jun 06 14:54:10 CEST 2017
 * XML source: /Users/rem/git/webanno-new/webanno-plugin-exmaralda/src/main/resources/desc/type/TeiTranscript.xml
 * @generated */
public class PlayableAnchor extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PlayableAnchor.class);
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
  protected PlayableAnchor() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public PlayableAnchor(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public PlayableAnchor(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public PlayableAnchor(JCas jcas, int begin, int end) {
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
  //* Feature: Info

  /** getter for Info - gets 
   * @generated
   * @return value of the feature 
   */
  public String getInfo() {
    if (PlayableAnchor_Type.featOkTst && ((PlayableAnchor_Type)jcasType).casFeat_Info == null)
      jcasType.jcas.throwFeatMissing("Info", "de.uhh.lt.webanno.exmaralda.type.PlayableAnchor");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PlayableAnchor_Type)jcasType).casFeatCode_Info);}
    
  /** setter for Info - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setInfo(String v) {
    if (PlayableAnchor_Type.featOkTst && ((PlayableAnchor_Type)jcasType).casFeat_Info == null)
      jcasType.jcas.throwFeatMissing("Info", "de.uhh.lt.webanno.exmaralda.type.PlayableAnchor");
    jcasType.ll_cas.ll_setStringValue(addr, ((PlayableAnchor_Type)jcasType).casFeatCode_Info, v);}    
   
    
  //*--------------*
  //* Feature: AnchorID

  /** getter for AnchorID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAnchorID() {
    if (PlayableAnchor_Type.featOkTst && ((PlayableAnchor_Type)jcasType).casFeat_AnchorID == null)
      jcasType.jcas.throwFeatMissing("AnchorID", "de.uhh.lt.webanno.exmaralda.type.PlayableAnchor");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PlayableAnchor_Type)jcasType).casFeatCode_AnchorID);}
    
  /** setter for AnchorID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnchorID(String v) {
    if (PlayableAnchor_Type.featOkTst && ((PlayableAnchor_Type)jcasType).casFeat_AnchorID == null)
      jcasType.jcas.throwFeatMissing("AnchorID", "de.uhh.lt.webanno.exmaralda.type.PlayableAnchor");
    jcasType.ll_cas.ll_setStringValue(addr, ((PlayableAnchor_Type)jcasType).casFeatCode_AnchorID, v);}    
  }

    