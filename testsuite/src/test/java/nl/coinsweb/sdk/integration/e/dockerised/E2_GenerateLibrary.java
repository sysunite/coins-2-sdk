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
import java.util.List;

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
public class E2_GenerateLibrary {

  protected static final Logger log = LoggerFactory.getLogger(E2_GenerateLibrary.class);




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




    File dockerfile = IntegrationHelper.getResourceFile("E2", "Dockerfile");


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

    CreateContainerResponse creation = dockerClient.createContainerCmd(imageId)
      .withEnv("COINSVERSION=1.1.603")
      .withExposedPorts(tcp80)
      .withPortBindings(portBindings)
      .withBinds(
        new Bind(hostProjectPath, new Volume("/opt/project")),
        new Bind(hostTestPath, new Volume("/opt/test"))
      )
      .exec();


    new Container();

    log.info("Created container {}", creation.toString());

    assertThat(creation.getId(), not(isEmptyString()));
    log.info(creation.getId());

    dockerClient.startContainerCmd(creation.getId()).exec();



    // Wait before the container stops
    boolean notStopped = true;
    while(notStopped) {
      List<Container> exitedContainers = dockerClient.listContainersCmd().withStatusFilter("exited").exec();
      for(Container container : exitedContainers) {
        notStopped &= !container.getId().equals(creation.getId());
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    dockerClient.removeContainerCmd(creation.getId()).exec();
    dockerClient.removeImageCmd(imageId);

  }
}