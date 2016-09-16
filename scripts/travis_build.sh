#!/usr/bin/env bash
#
# adapted from https://raw.githubusercontent.com/openzipkin/zipkin/master/travis/publish.sh
#
set -ex
readonly MVN_OPTS=-Dconfd.local.path.for.tests=/usr/local/bin/confd


function heartbeat() {
  # Some operations (for example Bintray GPG signing) run for a long time without output,
  # causing Travis to time out the build. This is an ugly hack to work around that.
  hard_timeout=3600  # 1 hour
  heartbeat_interval=10
  counter=0
  while [[ ${counter} -lt ${hard_timeout} ]] && kill -0 "$$" 2>/dev/null; do
    echo "(heartbeat $counter)"
	counter=$(($counter + $heartbeat_interval))
	sleep ${heartbeat_interval}
  done &
}

function increment_version() {
  # TODO this would be cleaner in release.versionPatterns
  local v=$1
  if [ -z $2 ]; then
     local rgx='^((?:[0-9]+\.)*)([0-9]+)($)'
  else
     local rgx='^((?:[0-9]+\.){'$(($2-1))'})([0-9]+)(\.|$)'
     for (( p=`grep -o "\."<<<".$v"|wc -l`; p<$2; p++)); do
        v+=.0; done; fi
  val=`echo -e "$v" | perl -pe 's/^.*'$rgx'.*$/$2/'`
  echo "$v" | perl -pe s/$rgx.*$'/${1}'`printf %0${#val}s $(($val+1))`/
}

function build_started_by_tag(){
  if [ "${TRAVIS_TAG}" == "" ]; then
    echo "[Publishing] This build was not started by a tag"
    return 1
  else
    echo "[Publishing] This build was started by the tag ${TRAVIS_TAG}"
    return 0
  fi
}

function build_started_by_docs_tag(){
  if [[ "${TRAVIS_TAG}" == docs-* ]]; then
    echo "[Publishing] This build was started the 'docs' tag ${TRAVIS_TAG}"
    return 0
  else
    echo "[Publishing] This build was not started by a 'docs' tag "
    return 1
  fi
}


function is_pull_request(){
  if [ "${TRAVIS_PULL_REQUEST}" != "false" ]; then
    echo "[is_pull_request] This is a Pull Request"
    return 0
  else
    echo "[is_pull_request] This is not a Pull Request"
    return 1
  fi
}

function is_travis_branch_master(){
  if [ "${TRAVIS_BRANCH}" = master ]; then
    echo "[Publishing] Travis branch is master"
    return 0
  else
    echo "[Not Publishing] Travis branch is not master"
    return 1
  fi
}

function check_travis_branch_equals_travis_tag(){
  #Weird comparison comparing branch to tag because when you 'git push --tags'
  #the branch somehow becomes the tag value
  #github issue: https://github.com/travis-ci/travis-ci/issues/1675
  if [ "${TRAVIS_BRANCH}" != "${TRAVIS_TAG}" ]; then
    echo "Travis branch does not equal Travis tag, which it should, bailing out."
    echo "  github issue: https://github.com/travis-ci/travis-ci/issues/1675"
    exit 1
  else
    echo "[Publishing] Branch (${TRAVIS_BRANCH}) same as Tag (${TRAVIS_TAG})"
  fi
}

function publish_docs() {
  echo "[Publishing Docs] Publishing..."
  mvn site -P publish-site --settings maven-settings.xml $MVN_OPTS
  echo "[Publishing Docs] Done"
}
function publish_snapshot(){
  echo "[Publishing Snapshot] Publishing..."
  mvn clean deploy --settings maven-settings.xml $MVN_OPTS
  echo "[Publishing Snapshot] Done"
}

function do_mvn_release(){
  # TODO this would be cleaner in release.versionPatterns
  major_minor_revision=$(echo "$TRAVIS_TAG" | cut -f1 -d-)
  qualifier=$(echo "$TRAVIS_TAG" | cut -f2 -d- -s)

  # do not increment if the version is tentative ex. 1.0.0-rc1
  if [[ -n "$qualifier" ]]; then
    new_version=${major_minor_revision}
  else
    new_version=$(increment_version "${major_minor_revision}")
  fi
  new_version="${new_version}-SNAPSHOT"

  echo "[Publishing] Creating release commits"
  echo "[Publishing]   Release version: ${TRAVIS_TAG}"
  echo "[Publishing]   Post-release version: ${new_version}"

  echo "Doing nothing for now, stay tune!"
#  git checkout -B master
#
#  PUBLISHING=true ./gradlew --info --stacktrace release -Prelease.useAutomaticVersion=true -PreleaseVersion=${TRAVIS_TAG} -PnewVersion=${new_version}
   publish_docs
#  echo "[Publishing] Done"
}

function run_tests(){
  echo "[Not Publishing] Running tests then exiting."
  mvn clean test $MVN_OPTS
}

#----------------------
# MAIN
#----------------------
action=run_tests

if ! is_pull_request; then
    if build_started_by_tag; then
        if build_started_by_docs_tag; then
            action=publish_docs
        else
            check_travis_branch_equals_travis_tag
            action=do_mvn_release
        fi
    elif is_travis_branch_master; then
        action=publish_snapshot
    fi
fi


heartbeat
$action
