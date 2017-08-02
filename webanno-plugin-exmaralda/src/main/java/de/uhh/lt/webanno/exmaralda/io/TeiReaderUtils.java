package de.uhh.lt.webanno.exmaralda.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeiReaderUtils {
    
    private TeiReaderUtils(){ /* DO NOT INSTANTIATE */ }
    
    public static final Pattern TIMEFINDER_PATTERN = Pattern.compile("[0-9]+([\\.\\,][0-9]+)?.*");
    public static final Pattern INTFINDER_PATTERN = Pattern.compile("[0-9]+");
    
    private static final Logger LOG = LoggerFactory.getLogger(TeiReaderUtils.class);
    
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
    
    public static <T extends Annotation> Stream<T> copyAnnotations(Stream<T> annotations, JCas toCas) {
        return annotations
          .map(a -> copyAnnotation(a, toCas))
          .filter(a -> a != null);
    }
    
    public static <T extends Annotation> T copyAnnotation(T a, JCas toCas){
        try {
            @SuppressWarnings("unchecked")
            T new_a = (T)(a.getClass().getConstructor(JCas.class).newInstance(toCas));
            // collect the methods of the annotation
            Method[] methods = a.getClass().getDeclaredMethods(); 
            Arrays.stream(methods)
            .filter(m -> m.getParameterCount() == 0 && m.getReturnType() != Void.TYPE && m.getName().startsWith("get") && m.getModifiers() == Modifier.PUBLIC) // only select getter methods with no parameters and a return type
            .filter(m -> !methodnamesToIgnore.contains(m.getName()))
            .forEach(getter -> {
                // get the setter method for the corresponding getter
                String setterName = "s" + getter.getName().substring(1); // replace 'get' with 'set'
                try {
                    Method setter = a.getClass().getMethod(setterName, getter.getReturnType());
                    Object result = getter.invoke(a);
                    setter.invoke(new_a, result);
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
                    LOG.warn("Could not invoke method {}:{} to ", new_a.getClass().getSimpleName(), setterName, e);
                }catch(NoSuchMethodException | SecurityException e) {
                    /* ignore */
                }
            });
            new_a.setBegin(((Annotation)a).getBegin());
            new_a.setEnd(((Annotation)a).getEnd());
            // new_a.addToIndexes(textview);
            return new_a;
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            LOG.warn("Could not copy annotation: {}", a.getClass().getName(), e);
            return null;
        }
    }

}
