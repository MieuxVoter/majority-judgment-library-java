# Majority Judgment Library for Java

[![MIT](https://img.shields.io/github/license/MieuxVoter/majority-judgment-library-java?style=for-the-badge)](./LICENSE)
[![Release](https://img.shields.io/github/v/release/MieuxVoter/majority-judgment-library-java?sort=semver&style=for-the-badge)](https://github.com/MieuxVoter/majority-judgment-library-java/releases)
[![Build Status](https://img.shields.io/github/actions/workflow/status/MieuxVoter/majority-judgment-library-java/maven.yml?style=for-the-badge)](https://github.com/MieuxVoter/majority-judgment-library-java/actions)
[![Code Quality](https://img.shields.io/codefactor/grade/github/MieuxVoter/majority-judgment-library-java?style=for-the-badge)](https://www.codefactor.io/repository/github/mieuxvoter/majority-judgment-library-java)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=MieuxVoter_majority-judgment-library-java&metric=coverage&style=for-the-badge)](https://sonarcloud.io/dashboard?id=MieuxVoter_majority-judgment-library-java)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MieuxVoter_majority-judgment-library-java&metric=alert_status&style=for-the-badge)](https://sonarcloud.io/dashboard?id=MieuxVoter_majority-judgment-library-java)
[![Join the Discord chat at https://discord.gg/rAAQG9S](https://img.shields.io/discord/705322981102190593.svg?style=for-the-badge)](https://discord.gg/rAAQG9S)

Test-driven java library to help deliberate (rank proposals) using [Majority Judgment](https://mieuxvoter.fr/index.php/decouvrir/?lang=en).

The goal is to be **scalable**, **reliable**, fast and extensible.
We therefore use a _score-based algorithm_ and _no floating-point arithmetic_ whatsoever.


## Features

- Supports billions of participants
- Supports thousands of proposals
- Handles default grades (static or normalized)
- No floating-point arithmetic
- Room for other deliberators (central, usual)


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

> A _Tally_ is the amount of judgments received per grade, by each proposal.

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

Got even more than that ?  Use `BigInteger`s !


### Using a static default grade

Want to set a static default grade ?  Use a `StaticDefaultTally` instead of a `Tally`.

```java
Integer amountOfJudges = 18;
Integer defaultGrade = 0;  // "worst" grade (usually "to reject")
TallyInterface tally = new StaticDefaultTally(new ProposalTallyInterface[] {
        // Amounts of judgments received of each grade, from "worst" grade to "best" grade
        new ProposalTally(new Integer[]{4, 5, 2, 1, 3, 1, 2}),  // Proposal A
        new ProposalTally(new Integer[]{3, 6, 2, 1, 3, 1, 2}),  // Proposal B
        // …
}, amountOfJudges, defaultGrade);
```


### Using normalized tallies

In some polls with a very high amount of proposals, where participants cannot be expected to judge every last one of them, it may make sense to normalize the tallies instead of using a default grade.

To that effect, use a `NormalizedTally` instead of a `Tally`.

```java
TallyInterface tally = new NormalizedTally(new ProposalTallyInterface[] {
        // Amounts of judgments received of each grade, from "worst" grade to "best" grade
        new ProposalTally(new Integer[]{4, 5, 2, 1, 3, 1, 2}),  // Proposal A
        new ProposalTally(new Integer[]{3, 6, 2, 1, 3, 1, 2}),  // Proposal B
        // …
});
```

> This normalization uses the Least Common Multiple, in order to skip floating-point arithmetic.


### Collect a Tally from judgments

It's usually best to use structured queries (eg: in SQL) directly in your database to collect the tallies, since it scales better with high amounts of participants, but if you must you can collect the tally directly from individual judgments, with a `CollectedTally`.

```java
Integer amountOfProposals = 2;
Integer amountOfGrades = 4;
DeliberatorInterface mj = new MajorityJudgmentDeliberator();
CollectedTally tally = new CollectedTally(amountOfProposals, amountOfGrades);

Integer firstProposal = 0;
Integer secondProposal = 1;
Integer gradeReject = 0;
Integer gradePassable = 1;
Integer gradeGood = 2;
Integer gradeExcellent = 3;

// Collect the judgments, one-by-one, with `collect()`
tally.collect(firstProposal, gradeReject);
tally.collect(firstProposal, gradeReject);
tally.collect(firstProposal, gradePassable);
tally.collect(firstProposal, gradePassable);
tally.collect(firstProposal, gradePassable);
tally.collect(firstProposal, gradeExcellent);
tally.collect(firstProposal, gradeExcellent);

tally.collect(secondProposal, gradeReject);
tally.collect(secondProposal, gradeReject);
tally.collect(secondProposal, gradeGood);
tally.collect(secondProposal, gradeGood);
tally.collect(secondProposal, gradeGood);
tally.collect(secondProposal, gradeExcellent);
tally.collect(secondProposal, gradeExcellent);

// …

ResultInterface result = mj.deliberate(tally);
```


## Run the test-suite

Install [maven](https://maven.apache.org), and run:

    mvn test

> Maven is available as a debian package: `apt install maven`

You can also use a runner in Eclipse.  (`CTRL+F11` to rerun)

