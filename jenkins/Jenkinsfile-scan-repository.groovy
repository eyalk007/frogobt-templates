pipeline {
    // Use your agent with installed package manager (npm, go, python, etc.)
    agent { label '<YOUR_AGENT_LABEL>' }
    
    triggers {
        // Scheduled scan - runs daily at midnight
        cron('0 0 * * *')
        
        // Webhook trigger - uncomment ONE provider block below based on your Git provider
        GenericTrigger(
            genericVariables: [
                // GitHub Push - Uncomment for GitHub
                //[key: 'JF_GIT_REPO', value: '$.repository.name'],
                //[key: 'JF_GIT_OWNER', value: '$.repository.owner.login'],
                //[key: 'JF_GIT_BASE_BRANCH', value: '$.ref'],

                // GitLab Push - Uncomment for GitLab
                //[key: 'JF_GIT_REPO', value: '$.project.name'],
                //[key: 'JF_GIT_OWNER', value: '$.project.namespace'],
                //[key: 'JF_GIT_BASE_BRANCH', value: '$.ref'],

                // Bitbucket Push - Uncomment for Bitbucket Server
                //[key: 'JF_GIT_REPO', value: '$.repository.slug'],
                //[key: 'JF_GIT_OWNER', value: '$.repository.project.key'],
                //[key: 'JF_GIT_BASE_BRANCH', value: '$.changes[0].ref.displayId'],

                // Azure Repos Push - Uncomment for Azure Repos
                //[key: 'JF_GIT_REPO', value: '$.resource.repository.name'],
                //[key: 'JF_GIT_OWNER', value: '$.resource.repository.project.name'],
                //[key: 'JF_GIT_BASE_BRANCH', value: '$.resource.refUpdates[0].name'],
                //[key: 'JF_GIT_PROJECT', value: '$.resource.repository.project.name'],
            ],
            causeString: 'Push Webhook',
             regexpFilterText: '$ref',
            //TRIGGERS ONLY ON MASTER/MAIN
             regexpFilterExpression: '^refs/heads/(master|main)$',
            // Webhook URL: https://<JENKINS_URL>/generic-webhook-trigger/invoke?token=<YOUR_WEBHOOK_TOKEN>
            token: '<YOUR_WEBHOOK_TOKEN>'
        )
    }
    
    environment {
        // [Mandatory] JFrog Platform URL
        // Example: 'https://mycompany.jfrog.io/'
        JF_URL = credentials('<YOUR_JF_URL_CREDENTIAL_ID>')
        
        // [Mandatory] JFrog Access Token with Xray read permissions
        JF_ACCESS_TOKEN = credentials('<YOUR_JF_ACCESS_TOKEN_CREDENTIAL_ID>')
        
        // [Mandatory] Git access token with repo permissions
        JF_GIT_TOKEN = credentials('<YOUR_GIT_TOKEN_CREDENTIAL_ID>')
        
        // [Mandatory] Git provider: 'github', 'gitlab', 'bitbucketServer', 'azureRepos'
        JF_GIT_PROVIDER = '<YOUR_GIT_PROVIDER>'
        
        // [Mandatory for on-prem Git] API endpoint
        // Examples: 'https://api.github.com', 'https://gitlab.company.com/api/v4'
        JF_GIT_API_ENDPOINT = '<YOUR_GIT_API_ENDPOINT>'
    }
    
    stages {
        stage('Frogbot Scan Repository') {
            steps {
                sh """
                    curl -fL "https://releases.jfrog.io/artifactory/frogbot/v3/[RELEASE]/getFrogbot.sh" | sh
                    ./frogbot scan-repository
                """
            }
        }
    }
}

