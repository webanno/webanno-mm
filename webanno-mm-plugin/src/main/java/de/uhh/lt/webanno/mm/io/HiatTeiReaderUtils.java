package de.uhh.lt.webanno.mm.io;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class HiatTeiReaderUtils {
    
    private HiatTeiReaderUtils(){ /* DO NOT INSTANTIATE */ }
    
    public static final Pattern TIMEFINDER_PATTERN = Pattern.compile("[0-9]+([\\.\\,][0-9]+)?.*");
    public static final Pattern INTFINDER_PATTERN = Pattern.compile("[0-9]+");
    
    private static final Logger LOG = LoggerFactory.getLogger(HiatTeiReaderUtils.class);
    
    private static final Set<String> methodnamesToIgnore = new HashSet<>(Arrays.asList("getTypeIndexID", "getCoveredText"));
    
    public static void logError(Logger log, Exception e){
        LOG.error("ERROR: {}:{} ", e.getClass().getName(), e.getMessage());
        Throwable cause = e.getCause(); 
        while(cause != null){
            LOG.error("ERROR cause: {}:{} ", cause.getClass().getName(), cause.getMessage());
            cause = cause.getCause();
        }
    }

    public static void logWarning(Logger log, Exception e){
        LOG.warn("ERROR: {}:{} ", e.getClass().getName(), e.getMessage());
        Throwable cause = e.getCause(); 
        while(cause != null){
            LOG.warn("ERROR cause: {}:{} ", cause.getClass().getName(), cause.getMessage());
            cause = cause.getCause();
        }
    }
    
    public static int findFirstNonSpace(CharSequence chars, int start){
        for(int i = start; i < chars.length()-1; i++)
            if(!Character.isWhitespace(chars.charAt(i)))
                return i;
        return -1;
    }

    public static int findLastNonSpace(CharSequence chars, int end){
        for(int i = end; i > 0; i--)
            if(!Character.isWhitespace(chars.charAt(i-1)))
                return i;
        return -1;
    }

    public static int findLastNonSpace(CharSequence chars){
        return findLastNonSpace(chars, chars.length());
    }
    
    public static Stream<Pair<Annotation, Annotation>> copyAnnotations(Stream<Annotation> annotations, JCas toCas) {
        return annotations
          .map(a -> Pair.of(a, copyAnnotation(a, toCas)));
    }
    
    public static Annotation copyAnnotation(Annotation a, JCas toJCas){
        Type aType = a.getType();
        CAS toCas = toJCas.getCas();
        AnnotationFS newAnnotationFS = toCas.createAnnotation(aType, a.getBegin(), a.getEnd());
        for(Feature f : aType.getFeatures()) {
            if(f.getRange().isPrimitive()) {
                String value = a.getFeatureValueAsString(f);
                newAnnotationFS.setFeatureValueFromString(f, value);
            } else if (!f.getShortName().equals("sofa")){
                FeatureStructure fs = a.getFeatureValue(f);
                newAnnotationFS.setFeatureValue(f, fs);
            }
        }
        toCas.addFsToIndexes(newAnnotationFS);
        return (Annotation) newAnnotationFS;
    }

}
