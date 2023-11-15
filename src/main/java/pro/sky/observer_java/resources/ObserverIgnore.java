package pro.sky.observer_java.resources;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObserverIgnore {
    private final Set<String> directories = new HashSet<>();
    private final Set<String> endsWith = new HashSet<>();

    public ObserverIgnore() {
        //Idea
        directories.addAll(List.of(
                ".idea",
                "cmake-build-",
                "out",
                ".idea_module",
                ".git",
                ".mvn",
                "mvnw"
                ));

        endsWith.addAll(List.of(
                ".iws",
                "atlassian-ide-plugin.xml",
                "com_crashlytics_export_strings.xml",
                "crashlytics.properties",
                "crashlytics-build.properties",
                "fabric.properties",
                "mvnw.cmd"
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
                "htmlcov",
                ".tox",
                ".nox",
                ".hypothesis",
                ".pytest_cache",
                "cover",
                "instance",
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

        endsWith.addAll(List.of(
                "$py.class",
                ".so",
                ".Python",
                ".installed.cfg",
                ".egg",
                "MANIFEST",
                ".manifest",
                ".spec",
                "pip-log.txt",
                "pip-delete-this-directory.txt",
                ".coverage",
                ".cache",
                "nosetests.xml",
                "coverage.xml",
                ".cover",
                ".mo",
                ".pot",
                ".log",
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
                ".sage.py",
                ".env",
                ".venv",
                ".spyderproject",
                ".spyproject",
                ".ropeproject",
                ".dmypy.json",
                "dmypy.json",
                "pyvenv.cfg",
                ".venv",
                "pip-selfcheck.json"
        ));

        //Java
        directories.addAll(List.of(
                ".data",
                "temp",
                "classes",
                "deploy",
                "javadoc",
                ".mtj.tmp"
        ));
        endsWith.addAll(List.of(
                "cwallet.sso.lck",
                ".class",
                ".log",
                ".ctxt",
                ".jar",
                ".war",
                ".nar",
                ".ear",
                ".zip",
                ".tar.gz",
                ".rar"
        ));
        //JS
        directories.addAll(List.of(
                "logs",
                "pids",
                "lib-cov",
                "coverage",
                "bower_components",
                "node_modules",
                "jspm_packages",
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

        endsWith.addAll(List.of(
                ".log",
                ".pid",
                ".seed",
                ".pid.lock",
                ".lcov",
                ".grunt",
                ".lock-wscript",
                "bower_components",
                ".tsbuildinfo",
                ".npm",
                ".eslintcache",
                ".stylelintcache",
                ".node_repl_history",
                ".tgz",
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
        //macOS
        endsWith.addAll(List.of(
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

    public Set<String> getDirectories() {
        return directories;
    }

    public Set<String> getEndsWith() {
        return endsWith;
    }

    public void addToDirectories(String directory){
        this.directories.add(directory);
    }

    public void addToEndsWith(String endsWith){
        this.directories.add(endsWith);
    }

    public boolean fileCheckIfIsInIgnored(File file){
        if(file.isDirectory()){
            for (String directory : directories) {
                if(file.getPath().contains(directory)){
                    return true;
                }
            }
            return false;
        }
        for (String s : endsWith) {
            if(file.getPath().endsWith(s)){
                return true;
            }
        }
        return false;
    }

    public boolean pathCheckIfIsInIgnored(String path){
        //File file = new File(path);
        for (String directory : directories) {
            if(path.contains(directory)){
                return true;
            }
        }
        return false;
    }
}
