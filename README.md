# dependencytreebuilder
This is an attempt to generate a dependency tree from a Maven POM file. This tool starts with the parent POM file, then recursively builds a dependency tree 
using the dependencies specified in the individual POM files. 

What is the motivation behind building this tool?
There were a few instances in the past when I had issues caused by incompatible third party transitive dependencies. It is sometimes
difficult to find the parent dependency that is downloading such transitive dependencies. The parent dependency needs to be identified to add exclusions. 
This tool is expected to generate a dependency tree that can be used to easily locate troublesome dependencies. 

There is already the Maven "dependency:tree" plugin that can be used to generate a dependency tree. However, I did not get the output in the format I needed. So I decided to create something of my own. I also used this as an opportunity to learn something new.

This tool work as a standalone spring boot application. I have built a web interface as well. 

I have tried to download the dependencies asynchronously and parallely using thread pools. I didn't intentionally use a message queue because I did not want to introduce additional dependency on message queues. I want this to run as a standalone application and don't want to set up a message queue to run this. This tool is using a cache, to make sure that a dependency is downloaded only once. Currently the cache is very rudimentary and I am using a Java HashMap as a cache. However, it is easy to change this code to use a third party cache.

This tool is still WIP. I still need to fix many issues to make this work.
