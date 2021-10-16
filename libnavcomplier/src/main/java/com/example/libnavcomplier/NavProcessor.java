package com.example.libnavcomplier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.libnavannotation.ActivityDestination;
import com.example.libnavannotation.FragementDestination;
import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;


@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.example.libnavannotation.FragementDestination","com.example.libnavannotation.ActivityDestination"})
public class NavProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
    private static final String OUTPUT_FILE_NAME = "destination.json";
    private FileOutputStream fos;
    private OutputStreamWriter writer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> fragementElements = roundEnvironment.getElementsAnnotatedWith(FragementDestination.class);
        Set<? extends Element> activityElements = roundEnvironment.getElementsAnnotatedWith(ActivityDestination.class);

        if (!fragementElements.isEmpty() || activityElements.isEmpty()){
            HashMap<String , JSONObject> destMap = new HashMap<>();
            handleDestination(fragementElements,FragementDestination.class,destMap);
            handleDestination(activityElements,ActivityDestination.class,destMap);

            // app/src/main/assets
            try {
                FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
                String resoucePath = fileObject.toUri().getPath();
                messager.printMessage(Diagnostic.Kind.NOTE,"resoucePath:" + resoucePath);
                String appPath = resoucePath.substring(0, resoucePath.indexOf("app") + 4);
                String assetPath = appPath + "src/main/assets/";

                File file = new File(assetPath);
                if (!file.exists()){
                    file.mkdir();
                }

                File outputFile = new File(file, OUTPUT_FILE_NAME);
                if (outputFile.exists()){
                    outputFile.delete();
                }
                outputFile.createNewFile();

                String content = JSON.toJSONString(destMap);
                fos = new FileOutputStream(outputFile);
                writer = new OutputStreamWriter(fos, "UTF-8");
                writer.write(content);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (writer!=null){
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return true;
    }

    private void handleDestination(Set<? extends Element> elements, Class<? extends Annotation> annotationClaz, HashMap<String, JSONObject> destMap) {
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            String pageUrl = null;
            String clazName = typeElement.getQualifiedName().toString();
            int id = Math.abs(clazName.hashCode());
            boolean needLogin = false;
            boolean asStarter = false;
            boolean isFragement = false;
            Annotation annotation = typeElement.getAnnotation(annotationClaz);
            if (annotation instanceof FragementDestination){
                FragementDestination dest = (FragementDestination) annotation;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
                isFragement = true;
            }else if (annotation instanceof ActivityDestination){
                ActivityDestination dest = (ActivityDestination) annotation;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
                isFragement = false;
            }

            if (destMap.containsKey(pageUrl)) {
                messager.printMessage(Diagnostic.Kind.ERROR,"不同的页面不允许使用相同的pageUrl：" + clazName);
            }else {
                JSONObject object = new JSONObject();
                object.put("id",id);
                object.put("needLogin",needLogin);
                object.put("asStarter",asStarter);
                object.put("pageUrl",pageUrl);
                object.put("clazName",clazName);
                object.put("isFragement",isFragement);
                destMap.put(pageUrl,object);
            }
        }


    }
}
