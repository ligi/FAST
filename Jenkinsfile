def flavorCombination1='ForGooglePlayWithExtras'
def flavorCombination2='ForGooglePlayNoExtras'

node {

 stage 'checkout'
 checkout scm

 stage 'UITest'
 lock('adb') {
   try {
    sh "./gradlew spoon${flavorCombination1}"
    sh "./gradlew spoon${flavorCombination2}"
   } catch(err) {
    currentBuild.result = FAILURE
   } finally {
     publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "android/build/spoon", reportFiles: '*/debug/index.html', reportName: 'Spoon'])
     step([$class: 'JUnitResultArchiver', testResults: 'android/build/spoon/*/debug/junit-reports/*.xml'])
   }
 }

 stage 'lint'
    try {
     sh "./gradlew clean lint${flavorCombination1}Release"
     sh "./gradlew clean lint${flavorCombination2}Release"
    } catch(err) {
     currentBuild.result = FAILURE
    } finally {
     publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'android/build/outputs/', reportFiles: "lint-results-*Release.html", reportName: 'Lint'])
    }
    
 stage 'test'
   try {
    sh "./gradlew clean test${flavorCombination1}DebugUnitTest"
    sh "./gradlew clean test${flavorCombination2}DebugUnitTest"
   } catch(err) {
    currentBuild.result = FAILURE
   } finally {
    publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'android/build/reports/tests/', reportFiles: "*/index.html", reportName: 'UnitTest'])
    step([$class: 'JUnitResultArchiver', testResults: 'android/build/test-results/*/*.xml'])
   }


 stage 'assemble'
  sh "./gradlew clean assemble${flavorCombination1}Release"
  sh "./gradlew clean assemble${flavorCombination2}Release"
  archive 'android/build/outputs/apk/*'
  archive 'android/build/outputs/mapping/*/release/mapping.txt'

}