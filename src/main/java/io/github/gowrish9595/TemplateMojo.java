package io.github.gowrish9595;

import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.interpret.RenderResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Mojo(name = "generate-files-for-templates", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class TemplateMojo extends AbstractMojo {

    @Parameter
    public String j2ResourcesDirectory;

    @Parameter
    public String outputDirectory;

    @Parameter
    public String contextFilePath;

    @Parameter(defaultValue = "false")
    public boolean isLStripBlocks;

    @Parameter(defaultValue = "true")
    public boolean isTrimBlocks;

    @Parameter(defaultValue = "true")
    public boolean isFailOnUnKnownVariables;

    JinjavaConfig jinjavaConfig = JinjavaConfig.newBuilder()
            .withFailOnUnknownTokens(isFailOnUnKnownVariables)
            .withTrimBlocks(isTrimBlocks)
            .withLstripBlocks(isLStripBlocks)
            .build();

    private final Jinjava jinjava = new Jinjava(jinjavaConfig);


    @Override
    public void execute() throws MojoExecutionException {
        Map<String, Object> variables = readVariableFile();
        List<File> filesInThePath = getResourceFilesInThePath(j2ResourcesDirectory, "j2");
        for (File file : filesInThePath) {
            String templateFileName = file.getName();
            String outputFileName = templateFileName.substring(0, templateFileName.length() - 3);
            String replacedContext = applyTemplatingOnTemplateFile(variables, file);
            writeToFile(outputFileName, replacedContext);
        }
    }

    private Map<String, Object> readVariableFile() throws MojoExecutionException {
        log.info("Reading variable File");
        File variableFilePath = new File(contextFilePath);
        try (FileInputStream fileInputStream = new FileInputStream(variableFilePath)) {
            Yaml yaml = new Yaml();
            return yaml.load(fileInputStream);
        } catch (FileNotFoundException e) {
            log.error("File not found in {}", variableFilePath, e);
            throw new MojoExecutionException("File Not Found", e);
        } catch (IOException e) {
            log.error("File not found in {}", variableFilePath, e);
            throw new MojoExecutionException("Error in reading context file", e);
        }
    }

    private List<File> getResourceFilesInThePath(String path, String fileExtension) {
        log.info("Retrieving j2 template file in path: {}", path);
        File file = new File(path);
        Collection<File> files = FileUtils.listFiles(file, new String[]{fileExtension}, false);
        log.debug("Files Found {}", files);
        return new ArrayList<>(files);
    }


    private String applyTemplatingOnTemplateFile(Map<String, Object> variables, File templateFile) throws MojoExecutionException {
        log.info("Applying templating on {}", templateFile.getName());
        try {
            String template = IOUtils.toString(templateFile.toURI().toURL(), StandardCharsets.UTF_8);
            RenderResult render = jinjava.renderForResult(template, variables);
            if (!render.hasErrors()) {
                log.info("Templating Successful for file:{}", templateFile.getName());
                return render.getOutput();
            } else {
                log.error("Error occurred while templating {}", templateFile.getName());
                render.getErrors()
                        .forEach(error -> log.error("reason: {}, message: {}", error.getReason().name(),
                                error.getMessage()));
                throw new MojoExecutionException("Error occurred while templating");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error occured while templating", e);
        }
    }

    private void writeToFile(String fileName, String value) throws MojoExecutionException {
        String outputPath = outputDirectory + fileName;
        try {
            FileUtils.writeStringToFile(new File(outputPath), value, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error occurred while writing file: {} to {}", fileName, outputPath, e);
            throw new MojoExecutionException("Error occurred while writing file to " + outputDirectory);
        }
    }

}
