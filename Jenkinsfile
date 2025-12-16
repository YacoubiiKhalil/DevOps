pipeline {
    agent any
    stages {
        stage('📁 DIAGNOSTIC RAPIDE') {
            steps {
                sh '''
                    echo "=== DIAGNOSTIC ==="
                    echo "Dossier: $(pwd)"
                    echo ""
                    echo "Fichiers:"
                    ls -la
                    echo ""
                    echo "Dockerfile(s) trouvé(s):"
                    find . -name "Dockerfile" 2>/dev/null
                    echo ""
                    echo "Structure docker/:"
                    ls -la docker/ 2>/dev/null || echo "Pas de dossier docker/"
                    echo ""
                    echo "Structure target/:"
                    ls -la target/ 2>/dev/null || echo "Pas de dossier target/"
                '''
            }
        }
    }
}
