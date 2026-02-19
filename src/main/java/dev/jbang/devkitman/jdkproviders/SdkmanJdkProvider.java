package dev.jbang.devkitman.jdkproviders;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;

import dev.jbang.devkitman.JdkDiscovery;
import dev.jbang.devkitman.JdkProvider;
import dev.jbang.devkitman.util.FileUtils;

/**
 * This JDK provider detects any JDKs that have been installed using the SDKMAN
 * package manager.
 */
public class SdkmanJdkProvider extends BaseFoldersJdkProvider {
	private static final String PROVIDER_SUFFIX = "-" + Discovery.PROVIDER_ID;
	private static final Pattern VALID_FOLDER_ID = Pattern.compile("^[a-zA-Z0-9._+-]+$");

	public SdkmanJdkProvider() {
		super(Paths.get(System.getProperty("user.home")).resolve(".sdkman/candidates/java"));
	}

	@Override
	public @NonNull String description() {
		return "The JDKs installed using the SDKMAN package manager.";
	}

	@Override
	protected boolean acceptFolder(@NonNull Path jdkFolder) {
		return super.acceptFolder(jdkFolder) && !FileUtils.isSameFolderLink(jdkFolder);
	}

	@Override
	protected @NonNull Path getJdkPath(@NonNull String id) {
		return super.getJdkPath(toFolderId(id));
	}

	@Override
	public boolean isValidId(@NonNull String id) {
		String folderId = toFolderId(id);
		return !"current".equals(folderId) && VALID_FOLDER_ID.matcher(folderId).matches();
	}

	@Override
	public @NonNull String jdkId(@NonNull Path jdkFolder) {
		return jdkFolder.getFileName().toString() + PROVIDER_SUFFIX;
	}

	private static @NonNull String toFolderId(@NonNull String id) {
		if (id.endsWith(PROVIDER_SUFFIX)) {
			return id.substring(0, id.length() - PROVIDER_SUFFIX.length());
		}
		return id;
	}

	public static class Discovery implements JdkDiscovery {
		public static final String PROVIDER_ID = "sdkman";

		@Override
		@NonNull
		public String name() {
			return PROVIDER_ID;
		}

		@Override
		public JdkProvider create(@NonNull Config config) {
			return new SdkmanJdkProvider();
		}
	}
}
