package nl.coinsweb.sdk.integration.e.dockerised;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

/**
 * Usecase E1:
 *
 *    Validate calls on cli
 *
 *
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class E1_RunViewer {

  protected static final Logger log = LoggerFactory.getLogger(E1_RunViewer.class);




  private DefaultDockerClientConfig config() {
    return config(null);
  }

  protected DefaultDockerClientConfig config(String password) {
    DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
      .withRegistryUrl("https://index.docker.io/v1/");
    if (password != null) {
      builder = builder.withRegistryPassword(password);
    }

    return builder.build();
  }











  @Test
  public void listing() {

    TestDockerCmdExecFactory dockerCmdExecFactory = new TestDockerCmdExecFactory(
      DockerClientBuilder.getDefaultDockerCmdExecFactory());

    DockerClient dockerClient = DockerClientBuilder.getInstance(config())
      .withDockerCmdExecFactory(dockerCmdExecFactory)
      .build();



    ExposedPort tcp80 = ExposedPort.tcp(80);

    Ports portBindings = new Ports();
    portBindings.bind(tcp80, Ports.Binding.empty());




    File dockerfile = IntegrationHelper.getResourceFile("E1", "Dockerfile");


    BuildImageResultCallback callback = new BuildImageResultCallback() {
      @Override
      public void onNext(BuildResponseItem item) {
        System.out.println("" + item);
        super.onNext(item);
      }
    };

    log.info(dockerfile.toString());


    String imageId = dockerClient.buildImageCmd(dockerfile)
      .exec(callback)
      .awaitImageId();

    String hostProjectPath = dockerfile.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().toString()+"/dist";
    String hostTestPath = dockerfile.getParentFile().toString();
    log.info(hostProjectPath);
    log.info(hostTestPath);

    CreateContainerResponse container = dockerClient.createContainerCmd(imageId)
      .withEnv("COINSVERSION=1.1.604")
      .withExposedPorts(tcp80)
      .withPortBindings(portBindings)
      .withBinds(
        new Bind(hostProjectPath, new Volume("/opt/project")),
        new Bind(hostTestPath, new Volume("/opt/test"))
      )
      .exec();



    log.info("Created container {}", container.toString());

    assertThat(container.getId(), not(isEmptyString()));
    log.info(container.getId());

    dockerClient.startContainerCmd(container.getId()).exec();

    try {
      Thread.sleep(120000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    dockerClient.stopContainerCmd(container.getId()).exec();
    dockerClient.removeContainerCmd(container.getId()).exec();
    dockerClient.removeImageCmd(imageId);

  }
}