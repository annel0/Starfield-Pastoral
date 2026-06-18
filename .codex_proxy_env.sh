# Local proxy environment for Codex workspace commands.
# Usage: source ./.codex_proxy_env.sh

export HTTP_PROXY="http://127.0.0.1:7890"
export HTTPS_PROXY="http://127.0.0.1:7890"
export ALL_PROXY="http://127.0.0.1:7890"

export http_proxy="$HTTP_PROXY"
export https_proxy="$HTTPS_PROXY"
export all_proxy="$ALL_PROXY"

export NO_PROXY="localhost,127.0.0.1,::1"
export no_proxy="$NO_PROXY"
