using namespace System.Text.Json

class Config {
    [ConfigEntry[]] $entries
}

class ConfigEntry {
    [string] $dirName
    [string] $jobYamlFileName
}

$configJson = Get-Content 'pre-deploy-config.json' -Raw
$configObj = [JsonSerializer]::Deserialize($configJson, [Config], $null)

Foreach ($entry IN $configObj.entries) {
    $dirName = $entry.dirName

    $parametersJson = Get-Content "$dirName/pre-deploy-templates/_parameters.json" -raw | ConvertFrom-Json
    $fileShareUniquePrefixTemplate = $parametersJson.parameters.fileShareUniquePrefix.value
    $random = [guid]::NewGuid().Guid.Split('-')[4];
    $fileShareUniquePrefix = $fileShareUniquePrefixTemplate.Replace("<random>", $random)
    $parametersJson.parameters.fileShareUniquePrefix.value = $fileShareUniquePrefix

    $jobFileShareTemplate = "$fileShareUniquePrefixTemplate<i>"
    $jobYamlRaw = Get-Content "$dirName/pre-deploy-templates/_job.yaml" -raw
    $fileSharesCount = ([regex]::Matches($jobYamlRaw, $jobFileShareTemplate)).count
    $parametersJson.parameters.fileSharesCount.value = $fileSharesCount
    $parametersJson | ConvertTo-Json | Out-File "$dirName/parameters.json"

    [regex]$jobFileShareTemplatePattern = $jobFileShareTemplate
    for ($i = 0 ; $i -lt $fileSharesCount ; $i++){
        $jobYamlRaw = $jobFileShareTemplatePattern.replace($jobYamlRaw, "$fileShareUniquePrefix$i", 1)
    }
    $jobYamlFileName = $entry.jobYamlFileName
    $jobYamlRaw | Out-File "$dirName/templates/$jobYamlFileName"
}
