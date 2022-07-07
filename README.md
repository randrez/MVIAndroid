# SC Trace Android Project

### Apollo

To pull the latest schema run:

Install the apollo cli

```npm install -g apollo```

Run this command to generate the schema.json in the root directory

```
    apollo schema:download --endpoint=https://stage.sctrace.com/gateway
```

or if you are working locally

```
    apollo schema:download --endpoint=http://localhost:4000/gateway
```

Make sure to overwrite the old generated schema.json located in:

```
    sctrace/sctrace-mobile/network/src/main/java/com/scgts/sctrace/network/graphql/
```

... by running the following command in the root directory:

```
    mv schema.json ./network/src/main/java/com/scgts/sctrace/network/graphql/schema.json
```

### Branching && Committing
- Please make sure all your branches follow the following formats:
  `name/feat/SCTRAC-149-docker-compose`
  `name/fix/SCTRAC-150-fix-docker-bug`
- Please make sure your commits follow
  [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
- Please make sure you have rebased (not merging master in) and squashed before merging your code (see `/documentation/rebasing.md` for more details on the rebase process)
