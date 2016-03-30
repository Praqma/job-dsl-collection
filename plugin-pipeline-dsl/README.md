# Plugin Pipeline DSL

~~Draft of Job DSL scripts that create a Jenkins build pipeline for a Jenkins plugin.~~

Prebuilt jobs for a quick and easy Jenkins plugin pipeline setup. 

### To do

- Refactor and separate common job logic

~~- Add convenient access to the configure block~~ 
Adding access to the configure block is trivial. 
It'd be impossible to control time of execution, so configuring build steps, publishers, etc. would be pointless. 


- Review pipeline and add missing components

# Jobs

### Integrate

Integrate the delivered branch into the integration branch using Pretested Integration.

### Test

Runs the thorougher tests.

### Analysis

Runs static analysis.

### Release

Manually triggered. Releases the plugin.

### JavaDoc
Generates JavaDoc and publishes it on GitHub pages.

### Sync

Updates the jenkinsci repository to be in line with our repository.
