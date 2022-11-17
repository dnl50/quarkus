package io.quarkus.cli.image;

import java.util.Map;
import java.util.Optional;

import picocli.CommandLine;
import picocli.CommandLine.ParentCommand;

@CommandLine.Command(name = "docker", sortOptions = false, showDefaultValues = true, mixinStandardHelpOptions = false, header = "Build a container image using Docker.", description = "%n"
        + "This command will build or push a container image for the project, using Docker.", footer = "%n"
                + "For example (using default values), it will create a container image using with REPOSITORY='${user.name}/<project.artifactId>' and TAG='<project.version>'.", headerHeading = "%n", commandListHeading = "%nCommands:%n", synopsisHeading = "%nUsage: ", parameterListHeading = "%n", optionListHeading = "Options:%n")
public class Docker extends BaseImageCommand {

    private static final String DOCKER = "docker";
    private static final String DOCKER_CONFIG_PREFIX = "quarkus.docker.";
    private static final String DOCKERFILE_JVM_PATH = "dockerfile-jvm-path";
    private static final String DOCKERFILE_NATIVE_PATH = "dockerfile-native-path";

    @CommandLine.Option(order = 7, names = { "--dockerfile" }, description = "The path to the Dockerfile.")
    public Optional<String> dockerFile;

    @ParentCommand
    BaseImageCommand parent;

    @Override
    public void populateImageConfiguration(Map<String, String> properties) {
        super.populateImageConfiguration(properties);
        properties.put(QUARKUS_CONTAINER_IMAGE_BUILDER, DOCKER);
        properties.put(QUARKUS_FORCED_EXTENSIONS, QUARKUS_CONTAINER_IMAGE_EXTENSION_KEY_PREFIX + DOCKER);
        dockerFile.ifPresent(d -> properties.put(DOCKER_CONFIG_PREFIX
                + (buildOptions.buildNative ? DOCKERFILE_NATIVE_PATH : DOCKERFILE_JVM_PATH), d));
    }

    @Override
    public Integer call() throws Exception {
        try {
            populateImageConfiguration(parent.getPropertiesOptions().properties);
            return parent.call();
        } catch (Exception e) {
            return output.handleCommandException(e, "Unable to build image: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Docker {imageOptions='" + imageOptions + "', dockerFile:'" + dockerFile.orElse("<none>") + "'}";
    }
}