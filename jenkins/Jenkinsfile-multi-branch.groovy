pipeline {
    agent { label 'YOUR-AGENT-LABEL' }
    
    environment {
    // Example: 'https://mycompany.jfrog.io/'
    JF_URL = '<YOUR_JFROG_PLATFORM_URL>'
    // Example: credentials('jfrog-access-token')
    JF_ACCESS_TOKEN = credentials('<YOUR_JFROG_ACCESS_TOKEN_CREDENTIAL_ID>')
    // Example: credentials('github-token')
    JF_GIT_TOKEN = credentials('<YOUR_GIT_TOKEN_CREDENTIAL_ID>')
    // Options: 'github', 'gitlab', 'bitbucketServer', 'azureRepos'
    JF_GIT_PROVIDER = '<YOUR_GIT_PROVIDER>'
    // Example: 'myorg' or 'mycompany'
    JF_GIT_OWNER = '<YOUR_GIT_OWNER>'
    // Example: 'my-repo'
    JF_GIT_REPO = '<YOUR_GIT_REPO>'
    // Example: 'https://api.github.com' or 'https://gitlab.mycompany.com/api/v4'
    JF_GIT_API_ENDPOINT = '<YOUR_GIT_API_ENDPOINT>'
    // Only required for Azure Repos. Example: 'MyProject'
    // JF_GIT_PROJECT = '<YOUR_AZURE_PROJECT>'
    // Comma-separated list of branches to scan
    AUTHORIZED_BRANCHES = 'main,
    }
    
    stages {
        stage('Frogbot Scan') {
            when {
                anyOf {
                    expression { env.CHANGE_ID != null }
                    expression { env.AUTHORIZED_BRANCHES.split(',').contains(env.BRANCH_NAME) }
                }
            }
            steps {
                sh """
                    curl -fL "https://releases.jfrog.io/artifactory/frogbot/v3/[VERSION]/getFrogbot.sh" -o frogbot
                    chmod +x frogbot
                    ./frogbot ${env.CHANGE_ID ? 'scan-pull-request' : 'scan-repository'}
                """
            }
        }
    }
}