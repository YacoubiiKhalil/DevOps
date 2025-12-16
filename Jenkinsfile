pipeline {
    agent any
    stages {
        stage('📁 DIAGNOSTIC STRUCTURE') {
            steps {
                sh '''
                    echo "=========================================="
                    echo "🧪 DIAGNOSTIC COMPLET JENKINS WORKSPACE"
                    echo "=========================================="
                    echo ""
                    echo "1️⃣ DOSSIER COURANT :"
                    pwd
                    echo ""
                    echo "2️⃣ LISTE FICHIERS (racine) :"
                    ls -la
                    echo ""
                    echo "3️⃣ RECHERCHE DOCKERFILE :"
                    find . -type f -name "Dockerfile" 2>/dev/null
                    echo ""
                    echo "4️⃣ EMPLACEMENT(S) DOCKERFILE TROUVÉ(S) :"
                    find . -type f -name "Dockerfile" -exec echo "   📍 {}" \; 2>/dev/null
                    echo ""
                    echo "5️⃣ CONTENU DU PREMIER DOCKERFILE :"
                    find . -type f -name "Dockerfile" -exec head -5 {} \; 2>/dev/null | head -10
                    echo ""
                    echo "6️⃣ STRUCTURE ARBRE :"
                    find . -type d -name "docker" -o -name "target" | sort
                    echo ""
                    echo "7️⃣ FICHIERS .JAR :"
                    find . -type f -name "*.jar" 2>/dev/null
                    echo ""
                    echo "=========================================="
                    echo "✅ DIAGNOSTIC TERMINÉ"
                    echo "=========================================="
                '''
            }
        }
    }
}
