package controllers;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.*;
import ninja.utils.HttpCacheToolkit;
import ninja.utils.MimeTypes;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Singleton
public class CustomAssetsController {

    private final static Logger logger = LoggerFactory
            .getLogger(AssetsController.class);

    public final static String ASSETS_DIR = "assets";

    public final static String FILENAME_PATH_PARAM = "fileName";

    private final MimeTypes mimeTypes;

    private final HttpCacheToolkit httpCacheToolkit;

    private final NinjaProperties ninjaProperties;

    private final AssetsControllerHelper assetsControllerHelper;

    @Inject
    public CustomAssetsController(AssetsControllerHelper assetsControllerHelper,
                            HttpCacheToolkit httpCacheToolkit,
                            MimeTypes mimeTypes,
                            NinjaProperties ninjaProperties) {
        this.assetsControllerHelper = assetsControllerHelper;
        this.httpCacheToolkit = httpCacheToolkit;
        this.mimeTypes = mimeTypes;
        this.ninjaProperties = ninjaProperties;
    }

    public Result serveStatic() {
        Object renderable = (Renderable) (context, result) -> {
            String fileName = getFileNameFromPathOrReturnRequestPath(context);
            URL url = getStaticFileFromAssetsDir(fileName);
            streamOutUrlEntity(url, context, result);
        };
        return Results.ok().render(renderable);
    }

    public Result serveWebJars() {
        Object renderable = (Renderable) (context, result) -> {
            String fileName = getFileNameMaybeMinified(context);
            URL url = getStaticFileFromMetaInfResourcesDir(fileName);
            streamOutUrlEntity(url, context, result);
        };
        return Results.ok().render(renderable);
    }

    private void streamOutUrlEntity(URL url, Context context, Result result) {
        if (url == null) {
            context.finalizeHeadersWithoutFlashAndSessionCookie(Results.notFound());
        } else {
            try {
                URLConnection urlConnection = url.openConnection();
                Long lastModified = urlConnection.getLastModified();
                httpCacheToolkit.addEtag(context, result, lastModified);

                if (result.getStatusCode() == Result.SC_304_NOT_MODIFIED) {
                    context.finalizeHeadersWithoutFlashAndSessionCookie(result);
                } else {
                    result.status(200);

                    String mimeType = mimeTypes.getContentType(context,
                            url.getFile());
                    if (mimeType != null && !mimeType.isEmpty()) {
                        result.contentType(mimeType);
                    }

                    ResponseStreams responseStreams = context
                            .finalizeHeadersWithoutFlashAndSessionCookie(result);
                    try (InputStream inputStream = urlConnection.getInputStream();
                         OutputStream outputStream = responseStreams.getOutputStream()) {
                        ByteStreams.copy(inputStream, outputStream);
                    }
                }
            } catch (IOException e) {
                logger.error("error streaming file", e);
            }
        }
    }

    private URL getStaticFileFromAssetsDir(String fileName) {
        URL url;
        if (ninjaProperties.isDev()
                && new File(assetsDirInDevModeWithoutTrailingSlash()).exists()) {
            String finalNameWithoutLeadingSlash = assetsControllerHelper.normalizePathWithoutLeadingSlash(fileName, false);
            File possibleFile = new File(
                    assetsDirInDevModeWithoutTrailingSlash()
                            + File.separator
                            + finalNameWithoutLeadingSlash);
            url = getUrlForFile(possibleFile);
        } else {
            String finalNameWithoutLeadingSlash = assetsControllerHelper.normalizePathWithoutLeadingSlash(fileName, true);
            url = this.getClass().getClassLoader()
                    .getResource(ASSETS_DIR
                            + "/"
                            + finalNameWithoutLeadingSlash);
        }
        return url;
    }

    private URL getUrlForFile(File possibleFileInSrc) {
        if (possibleFileInSrc.exists() && !possibleFileInSrc.isDirectory()) {
            try {
                return possibleFileInSrc.toURI().toURL();
            } catch(MalformedURLException malformedURLException) {
                logger.error("Error in dev mode while streaming files from src dir. ", malformedURLException);
            }
        }
        return null;
    }

    private URL getStaticFileFromMetaInfResourcesDir(String fileName) {
        String finalNameWithoutLeadingSlash = assetsControllerHelper.normalizePathWithoutLeadingSlash(fileName, true);
        return this.getClass().getClassLoader().getResource("META-INF/resources/webjars/" + finalNameWithoutLeadingSlash);
    }

    private static String getFileNameFromPathOrReturnRequestPath(Context context) {
        String fileName = context.getPathParameter(FILENAME_PATH_PARAM);
        if (fileName == null)
            fileName = context.getRequestPath();
        return fileName;
    }

    private String assetsDirInDevModeWithoutTrailingSlash() {
        String srcDir  = System.
                getProperty("user.dir")
                + File.separator
                + "src"
                + File.separator
                + "main"
                + File.separator
                + "java";
        return srcDir + File.separator + ASSETS_DIR;
    }

    private String getFileNameMaybeMinified(Context context) {
        String fileName = getFileNameFromPathOrReturnRequestPath(context);
        if (ninjaProperties.isProd() && fileName.endsWith(".js") && getStaticFileFromMetaInfResourcesDir(fileName.replace(".js", ".min.js")) != null)
            return fileName.replace(".js", ".min.js");
        else return fileName;
    }
}
