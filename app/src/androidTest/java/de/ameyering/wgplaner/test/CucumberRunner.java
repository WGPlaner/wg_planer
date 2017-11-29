package de.ameyering.wgplaner.test;

import cucumber.api.CucumberOptions;

@CucumberOptions(features = "features",
    glue = {"de.ameyering.wgplaner.test"},
    monochrome = true,
    plugin = {"pretty"}
)
public class CucumberRunner {
}
