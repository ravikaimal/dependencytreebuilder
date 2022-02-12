# dependencytreebuilder
This is an attempt to generate a dependency tree from a Maven POM file. This tool starts with the parent POM file, then recursively builds a dependency tree 
using the dependency informaton specified in the individual POM files. 

The motivation for building this tool is that, in the past some incompatible third transitive dependencies had caused issues with my application. It is sometimes
difficult to find the parent dependency that is downloading such transitive dependencies. This identification is important to correctly set exclusions.

This tool work as a standalone spring boot application. I have built a web interface as well. 

I have tried to download the dependencies asynchronously and parallely using thread pools. I didn't want to introduce a message queue to such a small application
asynchronous processing. This tool is using a cache, to make sure that a dependency is downloaded only once. Currently the cache is very rudimentary and I am using 
a Java HashMap as a cache. However, it is easy to change this code to use a third party cache.

It is still WIP. There are issues with complex POM files having multiple levels of dependencies.
