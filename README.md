# Majority Judgment Library for Java

[![MIT](https://img.shields.io/github/license/MieuxVoter/majority-judgment-library-java)](./LICENSE.md)
![Build Status](https://img.shields.io/github/workflow/status/MieuxVoter/majority-judgment-library-java/Java%20CI%20with%20Maven)
![Release](https://img.shields.io/github/v/release/MieuxVoter/majority-judgment-library-java?sort=semver)
[![Join the Discord chat at https://discord.gg/rAAQG9S](https://img.shields.io/discord/705322981102190593.svg)](https://discord.gg/rAAQG9S)

Test-driven java library to help deliberate using Majority Judgment.

The goal is to be **scalable**, **reliable**, fast and extensible.
We therefore use a _score-based algorithm_ and _no floating-point arithmetic_ whatsoever.


## Example Usage

Collect the **tallies** for each Proposal (aka. Candidate) by your own means,
provide them to the `MajorityJudgmentDeliberator`, and get back the **rank** of each Proposal.

Let's say you have the following tally:

|            | To Reject | Poor | Passable | Somewhat Good | Good | Very Good | Excellent |
|------------|-----------|------|----------|---------------|------|-----------|-----------|
| Proposal A |     4     |   5  |     2    |       1       |   3  |     1     |     2     |
| Proposal B |     3     |   6  |     2    |       2       |   2  |     1     |     2     |
|     …      |           |      |          |               |      |           |           |
|            |           |      |          |               |      |           |           |


``` java
DeliberatorInterface mj = new MajorityJudgmentDeliberator();
TallyInterface tally = new Tally(new ProposalTallyInterface[] {
        // Amounts of judgments received for each grade, from "worst" grade to "best" grade
        new ProposalTally(new Integer[]{4, 5, 2, 1, 3, 1, 2}),  // Proposal A
        new ProposalTally(new Integer[]{3, 6, 2, 1, 3, 1, 2}),  // Proposal B
        // …
}, 18);
ResultInterface result = mj.deliberate(tally);

// Each proposal result has a rank, and results are returned by input order
assert(2 == result.getProposalResults().length);
assert(2 == result.getProposalResults()[0].getRank());  // Proposal A
assert(1 == result.getProposalResults()[1].getRank());  // Proposal B
```

Got more than 2³² judges?  Use a `Long[]` in a `ProposalTally`.


## Roadmap

- [x] Unit-Tests
- [x] Deliberation algorithm
	- [x] Tally Analysis
	- [x] Score Calculus
	- [x] Ranking
- [x] Release v0.1.0
- [ ] Guess the amount of judges
- [ ] Allow defining a default grade
	- [ ] Static Grade (configurable)
	- [ ] Median Grade
	- [ ] Normalization (using smallest common multiple)
- [ ] Release v0.2.0
- [ ] Publish on package repositories
    - [ ] Gradle
    - [ ] Maven
    - [ ] … ? (please share your knowledge to help us!)
- [ ] Release v0.3.0
- [ ] Use it somewhere in another app, adjust API as needed (one last time)
- [ ] Release v1.0.0


## Gondor calls for Help!

We are not accustomed to Java library development and we'd love reviews from seasoned veterans !

Feel free to fork and request merges for your contributions and active readings !


## Run the test-suite

Install [maven](https://maven.apache.org), and run:

    mvn test

> Maven is available as a debian package: `apt install maven`

You can also use a runner in Eclipse.  (`CTRL+F11` to rerun)


## License

[MIT](./LICENSE.md)  →  _Do whatever you want except complain._

Majority Judgment itself is part of the Commons, obviously.


## Fund us

We'd love to invest more energy in Majority Judgment development.

Please consider funding us, every bit helps : https://www.paypal.com/donate/?hosted_button_id=QD6U4D323WV4S

