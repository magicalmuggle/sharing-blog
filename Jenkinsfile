// env 是 jenkins 提供的环境变量
String buildNumber = env.BUILD_NUMBER;
String timestamp = new Date().format('yyyyMMddHHmmss');
String projectName = env.JOB_NAME.split(/\//)[0];
// e.g awesome-project/release/RELEASE-1.0.0
String branchName = env.JOB_NAME.split(/\//)[1..-1].join(/\//);

println("${buildNumber} ${timestamp} ${projectName}");

String version = "${buildNumber}-${timestamp}-${projectName}";

node {
    checkout scm;

    // 检查构建参数是回滚还是正常构建
    if(params.BuildType=='Rollback') {
        return rollback()
    } else if(params.BuildType=='Normal'){
        return normalCIBuild(version)
    } else if(branchName == 'main'){
        setScmPollStrategyAndBuildTypes(['Normal', 'Rollback']);
    }
}

def normalCIBuild(String version) {
    stage 'test & package'

    sh('./mvnw clean package')

    stage('docker build')

    sh("docker build . -t 192.168.1.155:5000/sharing-blog:${version}")

    sh("docker push 192.168.1.155:5000/sharing-blog:${version}")

    stage('deploy')

    input 'deploy?' // 询问用户是否确认进行部署

    deployVersion(version)
}

def deployVersion(String version) {
    sh "ssh root@192.168.1.155 'docker rm -f sharing-blog && docker run --name sharing-blog -d -p 8080:8080 192.168.1.155:5000/sharing-blog:${version}'"
}

// 设置源代码管理系统轮询策略和构建类型
def setScmPollStrategyAndBuildTypes(List buildTypes) {
    def propertiesArray = [
            parameters([choice(choices: buildTypes.join('\n'), description: '', name: 'BuildType')]),
            pipelineTriggers([[$class: "SCMTrigger", scmpoll_spec: "* * * * *"]]) // cron 表达式，表示每分钟检查一次 git 仓库以进行构建
    ];
    properties(propertiesArray);
}

def rollback() {
    def dockerRegistryHost = "http://192.168.1.155:5000";
    def getAllTagsUri = "/v2/sharing-blog/tags/list";

    def responseJson = new URL("${dockerRegistryHost}${getAllTagsUri}")
            .getText(requestProperties: ['Content-Type': "application/json"]);

    println(responseJson)

    // {name:xxx,tags:[tag1,tag2,...]}
    Map response = new groovy.json.JsonSlurperClassic().parseText(responseJson) as Map;

    def versionsStr = response.tags.join('\n');

    def rollbackVersion = input(
            message: 'Select a version to rollback',
            ok: 'OK',
            parameters: [choice(choices: versionsStr, description: 'version', name: 'version')])

    println rollbackVersion
    deployVersion(rollbackVersion)
}
