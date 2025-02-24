param(
    [ValidateSet("start", "stop")]    
    [String]
    $mode,
    [String]
    $targetFolder = "."
)

try {
    docker --version | Out-Null
}
catch {
    Write-Error "A invocation of docker --version failed. This indicates that docker is not properly installed or running."
    Write-Error "Please check your docker invocation and try running the script again."
}

$CONTAINER_NAME = "ambitious_azsdk_test_proxy"
# this image should be pinned when merged to main in your repo.
$IMAGE_SOURCE = "azsdkengsys.azurecr.io/engsys/testproxy-lin:1035186"

function Get-Proxy-Container(){
    return (docker container ls -a --format "{{ json . }}" --filter "name=$CONTAINER_NAME" `
                | ConvertFrom-Json `
                | Select-Object -First 1)
}

$repoRoot = Resolve-Path $targetFolder

if ($mode -eq "start"){
    $proxyContainer = Get-Proxy-Container

    # if we already have one, we just need to check the state
    if($proxyContainer){
        if ($proxyContainer.State -eq "running")
        {
            Write-Host "Discovered an already running instance of the test-proxy!. Exiting"
            exit(0)
        }
    }
    # else we need to create it
    else {
        Write-Host "Attempting creation of Docker host $CONTAINER_NAME"
        docker container create -v ${repoRoot}:/etc/testproxy -p 5001:5001 -p 5000:5000 --name $CONTAINER_NAME $IMAGE_SOURCE
    }

    Write-Host "Attempting start of Docker host $CONTAINER_NAME"
    docker container start $CONTAINER_NAME
}

if ($mode -eq "stop"){
    $proxyContainer = Get-Proxy-Container

    if($proxyContainer){
        if($proxyContainer.State -eq "running"){
            Write-Host "Found a running instance of $CONTAINER_NAME, shutting it down."
            docker container stop $CONTAINER_NAME
        }
    }
}
