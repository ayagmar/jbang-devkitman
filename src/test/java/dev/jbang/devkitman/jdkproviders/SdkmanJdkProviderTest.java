package dev.jbang.devkitman.jdkproviders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.jbang.devkitman.BaseTest;
import dev.jbang.devkitman.Jdk;
import dev.jbang.devkitman.util.FileUtils;

public class SdkmanJdkProviderTest extends BaseTest {

	@Test
	void testDetectsSdkmanFolderNames() {
		Path sdkmanRoot = sdkmanJdksRoot();
		Path jdkFolder = sdkmanRoot.resolve("25.0.1-tem");
		initMockJdkDir(jdkFolder, "25.0.1");

		Jdk.InstalledJdk jdk = jdkManager("sdkman").getInstalledJdk("25+");
		assertThat(jdk, notNullValue());
		assertThat(jdk.provider(), instanceOf(SdkmanJdkProvider.class));
		assertThat(jdk.id(), is("25.0.1-tem-sdkman"));
	}

	@Test
	void testResolvesSuffixedAndUnsuffixedIds() {
		Path sdkmanRoot = sdkmanJdksRoot();
		Path jdkFolder = sdkmanRoot.resolve("21-tem");
		initMockJdkDir(jdkFolder, "21.0.7");

		Jdk.InstalledJdk bySuffixedId = jdkManager("sdkman").getInstalledJdk("21-tem-sdkman");
		Jdk.InstalledJdk byRawId = jdkManager("sdkman").getInstalledJdk("21-tem");

		assertThat(bySuffixedId, notNullValue());
		assertThat(byRawId, notNullValue());
		assertThat(bySuffixedId.home(), is(byRawId.home()));
	}

	@Test
	void testIgnoresCurrentLink() {
		Path sdkmanRoot = sdkmanJdksRoot();
		Path jdkFolder = sdkmanRoot.resolve("25.0.1-tem");
		initMockJdkDir(jdkFolder, "25.0.1");
		FileUtils.createLink(sdkmanRoot.resolve("current"), jdkFolder);

		List<Jdk.InstalledJdk> jdks = jdkManager("sdkman").listInstalledJdks();
		assertThat(jdks, hasSize(1));
		assertThat(jdks.get(0).id(), is("25.0.1-tem-sdkman"));
	}

	private Path sdkmanJdksRoot() {
		return Paths.get(System.getProperty("user.home")).resolve(".sdkman/candidates/java");
	}
}
