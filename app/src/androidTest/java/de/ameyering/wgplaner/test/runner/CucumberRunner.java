package de.ameyering.wgplaner.test.runner;

import cucumber.api.CucumberOptions;

@CucumberOptions(features = "features",
    glue = {"de.ameyering.wgplaner.test"},
    monochrome = true,
    plugin = {"pretty"}
)
public class CucumberRunner {
}
