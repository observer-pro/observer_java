package pro.sky.observer_java.resources;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.constants.CustomSocketEvents;
import pro.sky.observer_java.constants.JsonFields;
import pro.sky.observer_java.constants.StringFormats;
import pro.sky.observer_java.mapper.JsonMapper;

import java.io.File;
import java.util.*;

public class ObserverIgnore {
    private final Set<String> directories = new HashSet<>();
    private final Set<String> fileNames = new HashSet<>();
    private final Set<String> extensions = new HashSet<>();
    private final Set<String> endsWith = new HashSet<>();

    public ObserverIgnore() {
        //Idea
        directories.addAll(List.of(
                ".idea",
                "cmake-build-",
                "out",
                ".idea_modules"
        ));

        fileNames.addAll(List.of(
                "atlassian-ide-plugin.xml",
                "com_crashlytics_export_strings.xml",
                "crashlytics.properties",
                "crashlytics-build.properties",
                "fabric.properties"
        ));
        extensions.addAll(List.of(
                "iws"
        ));
        //Python
        directories.addAll(List.of(
                "__pycache__",
                "build",
                "develop-eggs",
                "dist",
                "downloads",
                "eggs",
                ".eggs",
                "lib",
                "lib64",
                "parts",
                "sdist",
                "var",
                "wheels",
                "python-wheels",
                ".egg-info",
                "htmlcov",
                ".tox",
                ".nox",
                ".hypothesis",
                ".pytest_cache",
                "cover",
                "instance",
                "_build",
                ".pybuilder",
                "target",
                "profile_default",
                "__pypackages__",
                "env",
                "venv",
                "ENV",
                "env.bak",
                "venv.bak",
                ".mypy_cache",
                ".pyre",
                ".pytype",
                "cython_debug"
        ));
        fileNames.addAll(List.of(
                ".Python",
                ".installed.cfg",
                "pip-log.txt",
                "pip-delete-this-directory.txt",
                "MANIFEST",
                ".coverage",
                ".cache",
                "nosetests.xml",
                "coverage.xml",
                "local_settings.py",
                "db.sqlite3",
                "db.sqlite3-journal",
                ".webassets-cache",
                ".scrapy",
                ".ipynb_checkpoints",
                "ipython_config.py",
                ".pdm.toml",
                "celerybeat-schedule",
                "celerybeat.pid",
                ".env",
                ".venv",
                ".spyderproject",
                ".spyproject",
                ".ropeproject",
                ".dmypy.json",
                "dmypy.json",
                "pyvenv.cfg",
                "pip-selfcheck.json"
        ));
        extensions.addAll(List.of(
                "so",
                "egg",
                "manifest",
                "spec",
                "cover",
                "mo",
                "pot",
                "log"
        ));
        endsWith.addAll(List.of(
                "$py.class"
        ));

        //Java
        directories.addAll(List.of(
                ".data",
                "temp",
                "classes",
                "deploy",
                "javadoc"
        ));
        fileNames.addAll(List.of(
                "cwallet.sso.lck",
                "hs_err_pid",
                "replay_pid"
        ));
        extensions.addAll(List.of(
                "log",
                "ctxt",
                "jar",
                "war",
                "nar",
                "ear",
                "zip",
                "rar"
        ));
        //JS
        directories.addAll(List.of(
                "node_modules",
                "jspm_packages",
                "web_modules",
                ".rpt2_cache",
                ".rts2_cache_cjs",
                ".rts2_cache_es",
                ".rts2_cache_umd",
                ".cache",
                ".serverless",
                ".fusebox",
                ".dynamodb",
                ".yarn"
        ));

        fileNames.addAll(List.of(
                "logs",
                "pids",
                "lib-cov",
                "coverage",
                ".nyc_output",
                ".grunt",
                "bower_components",
                ".lock-wscript",
                ".npm",
                ".eslintcache",
                ".stylelintcache",
                ".node_repl_history",
                ".yarn-integrity",
                ".env",
                ".env.development.local",
                ".env.test.local",
                ".env.production.local",
                ".env.local",
                ".cache",
                ".parcel-cache",
                ".next",
                "out",
                ".nuxt",
                "dist",
                ".temp",
                ".cache",
                ".docusaurus",
                ".tern-port",
                ".vscode-test"
        ));
        extensions.addAll(List.of(
                "log",
                "pid",
                "seed",
                "lcov",
                "tsbuildinfo",
                "tgz"
        ));
        //macOS
        fileNames.addAll(List.of(
                ".DS_Store",
                ".AppleDouble",
                ".LSOverride",
                "Icon",
                ".DocumentRevisions-V100",
                ".fseventsd",
                ".Spotlight-V100",
                ".TemporaryItems",
                ".Trashes",
                ".VolumeIcon.icns",
                ".com.apple.timemachine.donotpresent",
                ".AppleDB",
                ".AppleDesktop",
                "Network Trash Folder",
                "Temporary Items",
                ".apdisk"
        ));
    }

    public void addToDirectories(JSONArray directoryJson) throws JSONException {
        this.directories.addAll(JsonMapper.jsonArrayToStringList(directoryJson));
    }
    public void addToNames(JSONArray namesJson) throws JSONException {
        this.fileNames.addAll(JsonMapper.jsonArrayToStringList(namesJson));
    }

    public void addToExtensions(JSONArray extensionsJson) throws JSONException {
        this.extensions.addAll(JsonMapper.jsonArrayToStringList(extensionsJson));
    }
    
    public boolean checkIfIsInIgnored(File file) {
        if (file.isDirectory()) {
            for (String directory : directories) {

                if (file.getPath().contains(directory)) {
                    return true;
                }
            }
            return false;
        }

        String fileName = file.getName();
        if (fileNames.contains(fileName)) {
            return true;
        }

        String fileExtension = FilenameUtils.getExtension(fileName);
        if (extensions.contains(fileExtension)) {
            return true;
        }

        for (String end : endsWith) {
            if (file.getPath().endsWith(end)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkIfIsInIgnored(String path) {
        String baseUrl = FilenameUtils.getPath(path);

        //FOR TESTING TODO REMOVE

        StringBuilder sb = new StringBuilder();
        sb.append("Path is " + path + "\n");

        sb.append("GetPath is:" + FilenameUtils.getPath(path) + "\n");
        String messageText = sb.toString();

        JSONObject sendMessage = new JSONObject();
        try {
            sendMessage.put(JsonFields.ROOM_ID, ResourceManager.getInstance().getRoomId());
            sendMessage.put(JsonFields.CONTENT, messageText);
        } catch (JSONException exception) {
            //logger.warning("Connected panel JSON - " + exception.getMessage());
        }

        ResourceManager.getInstance().getmSocket().emit(CustomSocketEvents.MESSAGE_TO_MENTOR, sendMessage);
        if(baseUrl == null){
            baseUrl = "";
        }

        String extension = FilenameUtils.getExtension(path);

        String fileString = FilenameUtils.getBaseName(path)
                + "." + extension;

        if (extensions.contains(extension)) {
            return true;
        }

        if (fileNames.contains(fileString)) {
            return true;
        }

        for (String directory : directories) {
            if (baseUrl.contains(directory)) {
                return true;
            }
        }

        for (String s : endsWith) {
            if (fileString.endsWith(s)) {
                return true;
            }
        }
        return false;
    }


}
