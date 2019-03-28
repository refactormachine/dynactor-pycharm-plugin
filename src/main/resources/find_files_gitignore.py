import sys

import pathspec
import pathspec.util


def get_files_names(root, gitignore_rules):
    spec = pathspec.PathSpec.from_lines(pathspec.patterns.GitWildMatchPattern, gitignore_rules)
    all_files = set(pathspec.util.iter_tree(root, follow_links=False))
    return sorted(set(all_files) - set(spec.match_files(all_files)))


def main():
    assert len(sys.argv) == 2
    root = sys.argv[1]
    rules = list(sys.stdin)
    files = get_files_names(root, rules)
    print("\n".join(files))


if __name__ == '__main__':
    main()
