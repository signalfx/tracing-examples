<?php
$snippet_path = '/data/snippet.html';
$snippet = file_get_contents($snippet_path);

function replace_var($snippet, $name) {
    return str_replace("'" . $name . "'", "'" . getenv($name) . "'", $snippet);
}

$snippet = replace_var($snippet, 'SPLUNK_REALM');
$snippet = replace_var($snippet, 'SPLUNK_RUM_ACCESS_TOKEN');
$snippet = replace_var($snippet, 'SPLUNK_RUM_APPLICATION_NAME');
$snippet = replace_var($snippet, 'SPLUNK_RUM_ENVIRONMENT_NAME');

$template_path = '/var/www/html/example/templates/base.html.twig';
$template_backup_path = '/var/www/html/example/templates/base.html.twig.bak';

if (!file_exists($template_backup_path)) {
    copy($template_path, $template_backup_path);
}

$template = file_get_contents($template_backup_path);
$before_line = "        {% block javascripts %}";
$template_with_snippet = str_replace($before_line, $snippet . $before_line, $template);

file_put_contents($template_path, $template_with_snippet);
